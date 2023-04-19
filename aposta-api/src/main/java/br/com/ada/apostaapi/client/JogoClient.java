//package br.com.ada.apostaapi.client;
//
//import br.com.ada.apostaapi.model.Aposta;
//import br.com.ada.apostaapi.model.ApostaRequest;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@Bean
//public class JogoClient {
//    private final WebClient client;
//    private final ObjectMapper mapper;
//
//    public JogoClient(WebClient.Builder clientBuilder, ObjectMapper mapper) {
//        this.client = clientBuilder
//                .baseUrl("http://localhost:8080/jogos/")
//                .build();
//        this.mapper = mapper;
//    }
//
//    }
//
//}