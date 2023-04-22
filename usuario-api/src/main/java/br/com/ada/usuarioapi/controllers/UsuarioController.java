package br.com.ada.usuarioapi.controllers;

import br.com.ada.usuarioapi.model.Transacao;
import br.com.ada.usuarioapi.model.Usuario;
import br.com.ada.usuarioapi.model.UsuarioRequest;
import br.com.ada.usuarioapi.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.xml.crypto.dsig.TransformService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/usuario")
public class UsuarioController {
    private final UsuarioService service;

    @PostMapping
    public Mono<Usuario> novoUsuario(@RequestBody UsuarioRequest usuarioRequest){
        return Mono.defer(() -> service.save(usuarioRequest).subscribeOn(Schedulers.parallel())
                .doOnError(err -> log.error("Error ao salvar usuario - {}", err.getMessage()))
                .doOnNext(it -> log.info("usuario salvo com sucesso - {}", it)));
    }
    @GetMapping("/{id}")
    public Mono<Usuario> buscarUsuarioPorId(@PathVariable("id") String usuarioId){
        return Mono.defer(() -> service.get(usuarioId).subscribeOn(Schedulers.parallel())
                .doOnError(err -> log.error("Error ao buscar usuario - {}", err.getMessage()))
                .doOnNext(it -> log.info("Usuario encontrado com sucesso - {}", it)));
    }
    @GetMapping
    public Flux<Usuario> todosUsuarios(){
        return Flux.defer(service::getAll).subscribeOn(Schedulers.parallel())
                .doOnError(err -> log.error("Error ao buscar usuarios - {}", err.getMessage()))
                .doOnNext(it -> log.info("Usuarios recuperado com sucesso - {}", it));
    }

    @PatchMapping("/{id}/transacao")
    public Mono<Usuario> sacarSaldo(@PathVariable("id")String usuarioId, @RequestBody Transacao transacao){
        return Mono.defer(() -> service.valueTransaction(usuarioId, transacao)).subscribeOn(Schedulers.parallel())
                .doOnError(err -> log.error("Error na transção - {}", err.getMessage()))
                .doOnNext(it -> log.info("Transação realizada com sucesso - {}", it));
    }
}
