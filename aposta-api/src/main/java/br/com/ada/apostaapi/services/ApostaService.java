package br.com.ada.apostaapi.services;

import br.com.ada.apostaapi.client.JogoClient;
import br.com.ada.apostaapi.client.dto.JogoDTO;
import br.com.ada.apostaapi.model.Aposta;
import br.com.ada.apostaapi.model.ApostaRequest;
import br.com.ada.apostaapi.model.Status;
import br.com.ada.apostaapi.repositories.ApostaInMemoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class ApostaService {
    private final ApostaInMemoryRepository repository;
    private final JogoClient client;


    private Mono<Double> calcularCoeficiente(String id, String time) {
        return Mono.defer(() -> client.buscarJogoPorId(id)
               .map(jogoDTO ->{
                   if(!jogoDTO.status().equals(Status.ENCERRADO)) {
                       double coeficiente;
                       switch (jogoDTO.status()) {
                           case NAO_INICIADO -> coeficiente = (jogoDTO.mandante().equalsIgnoreCase(time)) ? 1.25 : 1.75;

                           case EM_ANDAMENTO -> {
                               if (jogoDTO.mandante().equalsIgnoreCase(time)) {
                                   if (jogoDTO.saldoGols() > 0) coeficiente = 1.5 - (jogoDTO.saldoGols() * 0.1);
                                   else if (jogoDTO.saldoGols() < 0) coeficiente = jogoDTO.saldoGols() * -1.0;
                                   else coeficiente = 1.5;
                               } else {
                                   if (jogoDTO.saldoGols() > 0) coeficiente = jogoDTO.saldoGols();
                                   else if (jogoDTO.saldoGols() < 0) coeficiente = 1.5 + (jogoDTO.saldoGols() * 0.1);
                                   else coeficiente = 2.0;
                                   ;
                               }
                           }
                           default -> coeficiente = 1.5;
                       }
                       return coeficiente;
                   }else throw new RuntimeException("Jogo ja encerado, impossivel fazer aposta");
                   }));


    }
    public Mono<Aposta> save(ApostaRequest apostaRequest) {

        return Mono.defer(() -> calcularCoeficiente(apostaRequest.jogoId(), apostaRequest.time())
                .map(client -> {
                   var aposta = Aposta.builder()
                        .apostaId(UUID.randomUUID())
                        .userId(UUID.fromString(apostaRequest.userId())) //pegar da api de usuarios
                        .jogoId(UUID.fromString(apostaRequest.jogoId())) // pegar da api de jogos
                        .valorApostado(apostaRequest.valorAposta())
                        .coefieciente(client) // pegar da api de jogos
                        .valorPremiacao(apostaRequest.valorAposta().multiply(BigDecimal.valueOf(client)))
                        .timeApostado(apostaRequest.time()) // pegar da api de joos ou transformar em enum
                        .status(Status.NAO_INICIADO) //handler para atualizr
                        .criacao(Instant.now())
                        .receber(false)
                        .build();
                    log.info("Salvando aposta -{}", aposta);
                   return repository.save(aposta);
                })).subscribeOn(Schedulers.boundedElastic());



//        return Mono.fromCallable(() -> {
//            log.info("Salvando aposta -{}", aposta);
//            return repository.save(aposta);
//        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Aposta> get(String uuid) {
        return Mono.defer(() -> {
            log.info("Buscando jogo - {}", uuid);
            return Mono.justOrEmpty(repository.get(UUID.fromString(uuid)));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<Aposta> getAll() {
        return Flux.defer(() -> {
            log.info("Buscando todos as apostas");
            return Flux.fromIterable(repository.getAll());
        }).subscribeOn(Schedulers.boundedElastic());
    }
    public Flux<Aposta> getAllByStatus(String status) {
        return Flux.defer(() -> {
            log.info("Buscando todos as Apostas pendentes");
            return Flux.fromIterable(repository.getAll().stream().filter(aposta -> aposta.getStatus().toString().equalsIgnoreCase(status)).toList());
        }).subscribeOn(Schedulers.boundedElastic());
    }

//    public Mono<Aposta> authorizeBet(String uuid, String status) {
//
//        return Mono.defer(() -> {
//            log.info("Iniciando autorizacao de aposta");
//            var ApostaOptional = repository.get(UUID.fromString(uuid));
//            ApostaOptional.ifPresent(aposta -> {
//                if (status.equalsIgnoreCase(Status.EM_ANDAMENTO.toString()) && aposta.getStatus().equals(Status.NAO_INICIADO)) {
//                    aposta.setStatus(Status.valueOf(status.toUpperCase()));
//                    aposta.setModificacao(Instant.now());
//                } else if (status.equalsIgnoreCase(Status.ENCERRADO.toString()) && aposta.getStatus().equals(Status.EM_ANDAMENTO)) {
//                    aposta.setStatus(Status.valueOf(status.toUpperCase()));
//                    aposta.setModificacao(Instant.now());
//                    if (aposta.getTime().equalsIgnoreCase("ABC")){ //pegar o vencedor pela API JOGOS
//                        aposta.setReceber(true);
//                    }
//                }
//            });
//            return Mono.justOrEmpty(ApostaOptional);
//        }).subscribeOn(Schedulers.boundedElastic());
//    }

    public Mono<Aposta> authorize(Aposta aposta) {

        return Mono.defer(() -> {
            log.info("Iniciando autorizacao de aposta");
            aposta.setStatus(Status.EM_ANDAMENTO);
            aposta.setModificacao(Instant.now());
//            repository.save(aposta);
            return Mono.justOrEmpty(aposta);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Aposta> conclude(Aposta aposta, JogoDTO jogoDTO) {
        return Mono.defer(() -> {
            log.info("Iniciando encerramento de aposta");
            aposta.setStatus(Status.ENCERRADO);
            aposta.setModificacao(Instant.now());
            if (aposta.getTimeApostado().equalsIgnoreCase(jogoDTO.vencedor())){
                aposta.setReceber(true);
            }
//            repository.save(aposta);
            return Mono.justOrEmpty(aposta);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}

