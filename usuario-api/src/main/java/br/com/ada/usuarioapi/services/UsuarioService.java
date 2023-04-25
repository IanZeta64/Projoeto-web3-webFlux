package br.com.ada.usuarioapi.services;

import br.com.ada.usuarioapi.enums.TipoTransacao;
import br.com.ada.usuarioapi.domain.Transacao;
import br.com.ada.usuarioapi.model.Usuario;
import br.com.ada.usuarioapi.requests.UsuarioRequest;
import br.com.ada.usuarioapi.repositories.UsarioReactiveRepository;
import br.com.ada.usuarioapi.responses.UsuarioResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {
    private final UsarioReactiveRepository repository;



    public Mono<UsuarioResponse> save(UsuarioRequest usuarioRequest){
        return Mono.defer(() -> {
            var usuario = Usuario.builder()
                    .documento(usuarioRequest.documento())
                    .email(usuarioRequest.email())
                    .nome(usuarioRequest.nome())
                    .senha(usuarioRequest.senha())
                    .saldo(BigDecimal.TEN)
                    .build();
            log.info("Salvando usuario - {}", usuario);
            return repository.save(usuario).map(Usuario::toResponse);
        }).subscribeOn(Schedulers.boundedElastic());

    }

    public Mono<UsuarioResponse> get(String usuarioId){
        return Mono.defer(() -> {
            log.info("Buscando usuario pelo id - {}", usuarioId);
            return repository.findById(usuarioId).map(Usuario::toResponse);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<UsuarioResponse> getAll(){
        return Flux.defer(() -> {
            log.info("Buscando todos os usuarios");
            return repository.findAll().map(Usuario::toResponse);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<UsuarioResponse> valueTransaction(String usuarioId, Transacao transacao){
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
                        return repository.save(user).map(Usuario::toResponse);
                    });
        }).subscribeOn(Schedulers.boundedElastic());
    }

}
