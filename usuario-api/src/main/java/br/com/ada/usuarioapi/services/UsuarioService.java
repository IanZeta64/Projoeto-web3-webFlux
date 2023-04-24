package br.com.ada.usuarioapi.services;

import br.com.ada.usuarioapi.model.TipoTransacao;
import br.com.ada.usuarioapi.model.Transacao;
import br.com.ada.usuarioapi.model.Usuario;
import br.com.ada.usuarioapi.model.UsuarioRequest;
import br.com.ada.usuarioapi.repositories.UsarioReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {
    private final UsarioReactiveRepository repository;

    public Mono<Usuario> save(UsuarioRequest usuarioRequest){
        return Mono.defer(() -> {
            var usuario = Usuario.builder()
//                    .usuarioId(UUID.randomUUID())
                    .documento(usuarioRequest.documento())
                    .nome(usuarioRequest.nome())
                    .saldo(BigDecimal.TEN)
                    .build();
            log.info("Salvando usuario - {}", usuario);
            return repository.save(usuario);
        }).subscribeOn(Schedulers.boundedElastic());

    }

    public Mono<Usuario> get(String usuarioId){
        return Mono.defer(() -> {
            log.info("Buscando usuario pelo id - {}", usuarioId);
            return repository.findById(usuarioId);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<Usuario> getAll(){
        return Flux.defer(() -> {
            log.info("Buscando todos os usuarios");
            return repository.findAll();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Usuario> valueTransaction(String usuarioId, Transacao transacao){
        return Mono.defer( () -> {
            log.info("Buscando usuario pelo id - {}", usuarioId);
            return repository.findById(usuarioId)
                    .flatMap(user ->{
                if (transacao.tipoTransacao().equals(TipoTransacao.SAQUE)) {
                    if (user.getSaldo().doubleValue() >= transacao.valorTransacao().doubleValue()) {
                        user.setSaldo(user.getSaldo().subtract(transacao.valorTransacao()));
                        log.info("Sacando valor de usuario - {}", usuarioId);
                    } else {
                        throw new RuntimeException("Saldo insuficiente");
                    }
                } else if (transacao.tipoTransacao().equals(TipoTransacao.DEPOSITO)) {
                    user.setSaldo(user.getSaldo().add(transacao.valorTransacao()));
                    log.info("Depositando valor de usuario - {}", usuarioId);
                }else {
                    throw new RuntimeException("Tipo de transacao invalida.");
                }
                        return repository.save(user);
                    });
        }).subscribeOn(Schedulers.boundedElastic());
    }

//    public Mono<Usuario> valueTransaction(String usuarioId, Transacao transacao){
//        return Mono.defer( () -> {
//            log.info("Buscando usuario pelo id - {}", usuarioId);
//            var usuarioOptional = repository.get(UUID.fromString(usuarioId));
//            usuarioOptional.ifPresent(user ->{
//                if (transacao.tipoTransacao().equals(TipoTransacao.SAQUE)) {
//                    if (user.getSaldo().doubleValue() >= transacao.valorTransacao().doubleValue()) {
//                        user.setSaldo(user.getSaldo().subtract(transacao.valorTransacao()));
//                        log.info("Sacando valor de usuario - {}", usuarioId);
//                    } else {
//                        throw new RuntimeException("Saldo insuficiente");
//                    }
//                } else if (transacao.tipoTransacao().equals(TipoTransacao.DEPOSITO)) {
//                    user.setSaldo(user.getSaldo().add(transacao.valorTransacao()));
//                    log.info("Depositando valor de usuario - {}", usuarioId);
//                }else {
//                    throw new RuntimeException("Tipo de transacao invalida.");
//                }
//            });
//            return  Mono.justOrEmpty(usuarioOptional);
//        }).subscribeOn(Schedulers.boundedElastic());
//    }

}
