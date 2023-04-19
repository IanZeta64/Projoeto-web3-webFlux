package br.com.ada.jogosapi.services;
import br.com.ada.jogosapi.model.Jogo;
import br.com.ada.jogosapi.model.Status;
import br.com.ada.jogosapi.repositories.JogoInMemoryRepository;
import br.com.ada.jogosapi.requests.JogoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JogoService {
//    private final JogoRepository repository;

    private final JogoInMemoryRepository repository;
    public Mono<Jogo> save(JogoRequest jogoRequest){
        var jogo = Jogo.builder()
                .uuid(UUID.randomUUID())
                .mandante(jogoRequest.mandante())
                .visitante(jogoRequest.visitante())
                .golsPorMandante(0L)
                .golsPorVisitante(0L)
                .saldoGols(0L)
                .dataHoraJogo(LocalDateTime.parse(jogoRequest.dataHoraJogo(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .status(Status.NAO_INICIADO)
                .build();

        return Mono.fromCallable(() ->{
            log.info("Salvando jogo -{}", jogo);
            return repository.save(jogo);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Jogo> get(String uuid){
        return Mono.defer( () -> {
            log.info("Buscando jogo - {}", uuid);
            return Mono.justOrEmpty(repository.get(UUID.fromString(uuid)));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    public Flux<Jogo> getAll(){
        return Flux.defer( () -> {
            log.info("Buscando todos os jogos");
            return Flux.fromIterable(repository.getAll());
        }).subscribeOn(Schedulers.boundedElastic());
    }
    public Flux<Jogo> getAllPerStatus(String status){
        return Flux.defer( () -> {
            log.info("Buscando todos os jogos");
            return Flux.fromIterable(repository.getAll().stream()
                    .filter(jogo -> jogo.getStatus().toString().equalsIgnoreCase(status)).toList());
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Jogo> scoreGoal(String uuid, String time){
        return Mono.defer(() -> {
            log.info("Iniciando alteracao de placar");
            var jogoOptional = repository.get(UUID.fromString(uuid));
                    jogoOptional.ifPresent(jogo -> {
                       if (jogo.getStatus().equals(Status.EM_ANDAMENTO)){
                           if(time.equalsIgnoreCase("mandante")){
                               jogo.setGolsPorMandante(jogo.getGolsPorMandante()+1);
                               jogo.setSaldoGols(jogo.getSaldoGols()+1);
                           } else if (time.equalsIgnoreCase("visitante")) {
                               jogo.setGolsPorVisitante(jogo.getGolsPorVisitante()+1);
                               jogo.setSaldoGols(jogo.getSaldoGols()-1);
                           }
                       }else{
                           throw new RuntimeException("Jogo encerrado ou nao iniciado");
                       }
                    });
                    return Mono.justOrEmpty(jogoOptional);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Jogo> updateStatus(String uuid, String status){
        return Mono.defer(() -> {
            log.info("Iniciando atualizacao de status");
            var jogoOptional = repository.get(UUID.fromString(uuid));
            jogoOptional.ifPresent(jogo -> {
                if(status.equalsIgnoreCase(Status.EM_ANDAMENTO.toString()) && jogo.getStatus().equals(Status.NAO_INICIADO)){
                    jogo.setStatus(Status.valueOf(status.toUpperCase()));
                    jogo.setInicioPartida(Instant.now());
                }else if(status.equalsIgnoreCase(Status.ENCERRADO.toString()) && jogo.getStatus().equals(Status.EM_ANDAMENTO)){
                    jogo.setStatus(Status.valueOf(status.toUpperCase()));
                    jogo.setFinalPartida(Instant.now());
                    if (jogo.getSaldoGols() > 0){
                        jogo.setVencedor(jogo.getMandante());
                    }else if (jogo.getSaldoGols() < 0){
                        jogo.setVencedor(jogo.getVisitante());
                    }else{
                        jogo.setVencedor("Empate");
                    }
                }


            });
            return Mono.justOrEmpty(jogoOptional);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Jogo> update(JogoRequest jogoRequest, String uuid) {
        return Mono.defer(() ->{
            log.info("Atualizando jogo -{}", jogoRequest);
            var jogoOptional = repository.get(UUID.fromString(uuid));
            jogoOptional.ifPresent(jogo -> {
               jogo.setMandante(jogoRequest.mandante());
               jogo.setVisitante(jogoRequest.visitante());
               jogo.setDataHoraJogo(LocalDateTime.parse(jogoRequest.dataHoraJogo(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
               repository.save(jogo);
            });
            return Mono.justOrEmpty(jogoOptional);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
