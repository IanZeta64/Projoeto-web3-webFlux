package br.com.ada.apostaapi.client;

import br.com.ada.apostaapi.client.dto.JogoDTO;
import br.com.ada.apostaapi.exceptions.ClientErrorException;
import br.com.ada.apostaapi.exceptions.GameNotFoundException;
import br.com.ada.apostaapi.exceptions.MicrosservicesConnectionException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JogoClient {

    private static final String JOGOS_API_URL = "http://localhost:8081";

    private final WebClient client;
    private final ReactiveCircuitBreaker reactiveCircuitBreaker;

    public JogoClient(WebClient.Builder builder, ReactiveCircuitBreakerFactory<?,?> reactiveCircuitBreaker) {
        this.client = builder.baseUrl(JOGOS_API_URL).build();
        this.reactiveCircuitBreaker = reactiveCircuitBreaker.create("aposta-api-circuit-breaker");
    }

    public Mono<JogoDTO> buscarJogoPorId(String jogoId) {
        return this.reactiveCircuitBreaker.run(client
                .get()
                .uri("/jogos/" + jogoId)
                .exchangeToMono(result -> {
                    if (result.statusCode().is2xxSuccessful()) {
                        return result.bodyToMono(JogoDTO.class);
                    } else {
                        return Mono.error(new GameNotFoundException("Jogo inexistente!"));
                    }
                }).subscribeOn(Schedulers.boundedElastic()), this::fallbackMethod);
        }
    private  <T> Mono<T> fallbackMethod(Throwable throwable) {
        return Mono.defer(() -> {
            log.error("ENTRANDO NO METODO FALLBACK");
           if (throwable.getClass().equals(GameNotFoundException.class)) {
               return Mono.error(new GameNotFoundException("Jogo inexistente!"));
           }else{
               return Mono.error(new MicrosservicesConnectionException("Erro de conexao com microsservicos. Chamando fallback."));
           }
        });
    }
    }