package br.com.ada.jogosapi.controllers;

import br.com.ada.jogosapi.model.Jogo;
import br.com.ada.jogosapi.requests.JogoRequest;
import br.com.ada.jogosapi.services.JogoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jogos")
@Slf4j
public class JogoController {
    private final JogoService service;

    @PostMapping
    public Mono<Jogo> novaPartida(@RequestBody JogoRequest jogoRequest){
        return Mono.defer(() ->
            service.save(jogoRequest).subscribeOn(Schedulers.parallel())
                    .doOnError(err -> log.error("Error ao salvar jogo - {}", err.getMessage()))
                    .doOnNext(it -> log.info("Jogo salvo com sucesso - {}", it)));

    }
    @PutMapping("/{id}")
    public Mono<Jogo> alterarPartida(@RequestBody JogoRequest jogoRequest, @PathVariable("id") String uuid){
        return Mono.defer(() ->
                service.update(jogoRequest, uuid).subscribeOn(Schedulers.parallel())
                        .doOnError(err -> log.error("Error ao alterar jogo - {}", err.getMessage()))
                        .doOnNext(it -> log.info("Jogo alterado com sucesso - {}", it)));

    }
    @GetMapping
    public Flux<Jogo> getTodosOsJogos(){
        return Flux.defer(service::getAll).doOnComplete(() -> log.info("Jogos buscado com sucesso"));
    }
    @GetMapping(params = "status")
    public Flux<Jogo> getTodosOsJogosEmAndamento(@RequestParam("status") String status){
        return Flux.defer(() -> service.getAllPerStatus(status)).doOnComplete(() -> log.info("Jogos buscado com sucesso"));
    }
    @GetMapping("/{id}")
    public Mono<Jogo> getPartida(@PathVariable("id") String uuid){
        return Mono.defer(() -> service.get(uuid).subscribeOn(Schedulers.parallel())
                .doOnError(err -> log.error("Error ao buscar jogo - {}", err.getMessage()))
                .doOnNext(it -> log.info("Jogo recuperado com sucesso - {}", it)));
    }
    @PatchMapping(value = "/{id}", params = "time")
    public Mono<Jogo> marcarGol(@PathVariable("id") String uuid, @RequestParam("time")String time){//time = 1 mandante e 2 visitante
        return Mono.defer(() -> service.scoreGoal(uuid, time)).doOnError(err -> log.error("Error ao alterar placar - {}", err.getMessage()))
                .doOnNext(it -> log.info("Placar alterado - {}", it));
    }
    @PatchMapping(value = "/{id}", params = "status")
    public Mono<Jogo> alterarStatus(@PathVariable("id") String uuid, @RequestParam("status")String status){//time = 1 mandante e 2 visitante
        return Mono.defer(() -> service.updateStatus(uuid, status)).doOnError(err -> log.error("Error ao atualizar status - {}", err.getMessage()))
                .doOnNext(it -> log.info("Status alterado - {}", it));
    }
}
