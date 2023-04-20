//package br.com.ada.jogosapi.client;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.util.UUID;
//
//@Component
//public class ApostaClient {
//
//    private static final String JOGOS_API_URL = "http://localhost:8080";
//
//    private final WebClient client;
//
//    public ApostaClient(WebClient.Builder builder) {
//        this.client = builder.baseUrl(JOGOS_API_URL).build();
//    }
//
//    public Mono<UUID> notificarJogoEncerrado(String jogoId) {
//        return client
//                .get()
//                .uri("/jogos/" + jogoId)
//                .exchangeToMono(result -> {
//                    if (result.statusCode().is2xxSuccessful()) {
//                        return result.bodyToMono(JogoDTO.class)
//                                .switchIfEmpty(Mono.error(new RuntimeException("Jogo inexistente!")));
//                    } else {
//                        return Mono.error(new RuntimeException("Erro na chamada"));
//                    }
//                });
//    }
//}