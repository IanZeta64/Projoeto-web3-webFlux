package br.com.ada.apostaapi.client;

import br.com.ada.apostaapi.client.dto.UsuarioDTO;
import br.com.ada.apostaapi.client.dto.TransacaoDTO;
import br.com.ada.apostaapi.exceptions.ClientErrorException;
import br.com.ada.apostaapi.exceptions.GameNotFoundException;
import br.com.ada.apostaapi.exceptions.MicrosservicesConnectionException;
import br.com.ada.apostaapi.exceptions.UserNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UsuarioClient {
    private static final String USUARIOS_API_URL = "http://localhost:8083";

    private final WebClient client;

    private final ReactiveCircuitBreaker reactiveCircuitBreaker;

    public UsuarioClient(WebClient.Builder builder, ReactiveCircuitBreakerFactory<?,?> reactiveCircuitBreaker) {
        this.client = builder.baseUrl(USUARIOS_API_URL).build();
        this.reactiveCircuitBreaker = reactiveCircuitBreaker.create("aposta-api-circuit-breaker");
    }

    public Mono<UsuarioDTO> buscarUsuarioPorId(String usuarioId) {
        return this.reactiveCircuitBreaker.run(client
                .get()
                .uri("/usuario/" + usuarioId)
                .exchangeToMono(result -> {
                    if (result.statusCode().is2xxSuccessful()) {
                        return result.bodyToMono(UsuarioDTO.class);

                    } else {
                        return Mono.error(new UserNotFoundException("Usuario inexistente!"));
                    }
                }), this::fallbackMethod);
    }

    private  <T> Mono<T> fallbackMethod(Throwable throwable) {
        return Mono.defer(() -> {
            log.error("ENTRANDO NO METODO FALLBACK");
            if (throwable.getClass().equals(UserNotFoundException.class)){
                return Mono.error(new UserNotFoundException("Usuario inexistente!"));
            }else{
                return Mono.error(new MicrosservicesConnectionException("Erro de conexao com microsservicos. Chamando fallback."));
            }
        });
    }


    public Mono<TransacaoDTO> transacao(String usuarioId, TransacaoDTO transacaoDTO){
        return this.reactiveCircuitBreaker.run(client
                .patch()
                .uri("/usuario/" + usuarioId + "/transacao")
                .bodyValue(transacaoDTO)
                .exchangeToMono(result -> {
                    if (result.statusCode().is2xxSuccessful()) {
                        return result.bodyToMono(TransacaoDTO.class)
                                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario inexistente!")));
                    } else {
                        return Mono.error(new ClientErrorException("Erro na chamada, verifique os dados inseidos"));
                    }
                }), this::fallbackMethod);
    }
}

