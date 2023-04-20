package br.com.ada.apostaapi.client;

import br.com.ada.apostaapi.client.dto.JogoDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JogoClient {

    private static final String JOGOS_API_URL = "http://localhost:8080";

    private final WebClient client;

    public JogoClient(WebClient.Builder builder) {
        this.client = builder.baseUrl(JOGOS_API_URL).build();
    }

    public Mono<JogoDTO> buscarJogoPorId(String jogoId) {
        return client
                .get()
                .uri("/jogos/" + jogoId)
                .exchangeToMono(result -> {
                    if (result.statusCode().is2xxSuccessful()) {
                        return result.bodyToMono(JogoDTO.class)
                                .switchIfEmpty(Mono.error(new RuntimeException("Jogo inexistente!")));
                    } else {
                        return Mono.error(new RuntimeException("Erro na chamada"));
                    }
                }).subscribeOn(Schedulers.boundedElastic());
        }

    public Flux<JogoDTO> buscarJogosEncerradosPorIds(List<String> jogosIds) {
        return Flux.fromIterable(jogosIds) // Converter a lista de IDs em um Flux
                .flatMap(jogoId -> client
                        .get()
                        .uri("/jogos/{id}", jogoId) // Utilize o ID do jogo para montar a URI de busca por ID
                        .retrieve()
                        .bodyToMono(JogoDTO.class)
                        .filter(jogo -> "encerrado".equalsIgnoreCase(jogo.status().toString()))) // Filtrar apenas os jogos encerrados
                .collectList() // Coletar os jogos encerrados em uma lista
                .flatMapMany(Flux::fromIterable).subscribeOn(Schedulers.boundedElastic())
                .doOnError(Throwable::getMessage);
    }

//    public Flux<JogoDTO> buscarJogosEncerrados() {
//        return client
//                .get()
//                .uri("/jogos?status=encerrado")
//                .retrieve()
//                .bodyToFlux(JogoDTO.class)
//                .switchIfEmpty(Flux.error(new RuntimeException("Jogo inexistente!")));
//
//        }
    }