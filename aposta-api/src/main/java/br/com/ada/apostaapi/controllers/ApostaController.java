package br.com.ada.apostaapi.controllers;
import br.com.ada.apostaapi.model.Aposta;
import br.com.ada.apostaapi.model.ApostaRequest;
import br.com.ada.apostaapi.services.ApostaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/apostas")
public class ApostaController {
    private final ApostaService service;

    @PostMapping
    public Mono<Aposta> novaAposta(@RequestBody ApostaRequest apostaRequest){
        return Mono.defer(() ->
                service.save(apostaRequest).subscribeOn(Schedulers.parallel())
                        .doOnError(err -> log.error("Error ao salvar aposta - {}", err.getMessage()))
                        .doOnNext(it -> log.info("Aposta salva com sucesso - {}", it)));
    }
    @GetMapping("/{id}")
    public Mono<Aposta> getAposta(@PathVariable("id") String uuid){
        return Mono.defer(() -> service.get(uuid).subscribeOn(Schedulers.parallel())
                .doOnError(err -> log.error("Error ao buscar aposta - {}", err.getMessage()))
                .doOnNext(it -> log.info("Aposta recuperado com sucesso - {}", it)));
    }
    @GetMapping
    public Flux<Aposta> getTodasAposta(){
        return Flux.defer(service::getAll).subscribeOn(Schedulers.parallel())
                .doOnError(err -> log.error("Error ao buscar apostas - {}", err.getMessage()))
                .doOnNext(it -> log.info("Apostas recuperado com sucesso - {}", it));
    }
    @GetMapping(params = "status")
    public Flux<Aposta> getTodasApostaPorStatus(@RequestParam("status")String status){
        return Flux.defer(() -> service.getAllByStatus(status)).subscribeOn(Schedulers.parallel())
                .doOnError(err -> log.error("Error ao buscar apostas - {}", err.getMessage()))
                .doOnNext(it -> log.info("Apostas recuperado com sucesso - {}", it));
    }

    @PatchMapping(value = "/{id}", params = "status")
    public Mono<Aposta> alterarStatus(@PathVariable("id") String uuid, @RequestParam("status")String status){//time = 1 mandante e 2 visitante
        return Mono.defer(() -> service.updateStatus(uuid, status)).doOnError(err -> log.error("Error ao atualizar status - {}", err.getMessage()))
                .doOnNext(it -> log.info("Status alterado - {}", it));
    }

}
