package br.com.ada.apostaapi.client;

import br.com.ada.apostaapi.client.dto.JogoDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class JogoClient {

    private static final String PRODUTOS_API_URL = "http://localhost:8080";

    private final WebClient client;

    public JogoClient(WebClient.Builder builder) {
        this.client = builder.baseUrl(PRODUTOS_API_URL).build();
    }

    public Mono<JogoDTO> buscarProdutoPorId(String jogoId) {
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
                });
        }
    }