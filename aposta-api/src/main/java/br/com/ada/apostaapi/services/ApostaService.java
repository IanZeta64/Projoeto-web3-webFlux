package br.com.ada.apostaapi.services;

import br.com.ada.apostaapi.client.JogoClient;
import br.com.ada.apostaapi.client.UsuarioClient;
import br.com.ada.apostaapi.client.dto.JogoDTO;
import br.com.ada.apostaapi.client.dto.TransacaoDTO;
import br.com.ada.apostaapi.enums.Premiacao;
import br.com.ada.apostaapi.enums.Status;
import br.com.ada.apostaapi.enums.TipoTransacao;
import br.com.ada.apostaapi.model.*;
import br.com.ada.apostaapi.requests.ApostaRequest;
import br.com.ada.apostaapi.repositories.ApostaRepository;
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
    private final ApostaRepository repository;
    private final JogoClient jogoClient;
    private final UsuarioClient usuarioClient;


    private Mono<Double> calcularCoeficiente(String id, String time) {
        return Mono.defer(() -> jogoClient.buscarJogoPorId(id)
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
                        .flatMap(coeficiente -> usuarioClient.buscarUsuarioPorId(apostaRequest.userId())
                                .flatMap(usuarioDTO -> {
                                    if (usuarioDTO.saldo().doubleValue() >= apostaRequest.valorAposta().doubleValue()) {
                                        var aposta = Aposta.builder()
//                                                .apostaId(UUID.randomUUID())
                                                .userId(apostaRequest.userId())
                                                .jogoId(apostaRequest.jogoId())
                                                .valorApostado(apostaRequest.valorAposta())
                                                .valorPremiacao(apostaRequest.valorAposta().multiply(BigDecimal.valueOf(coeficiente)))
                                                .coeficiente(coeficiente)
                                                .timeApostado(apostaRequest.time())
                                                .status(Status.NAO_INICIADO)
                                                .criacao(Instant.now())
                                                .premiacao(Premiacao.INDISPONIVEL)
                                                .build();
                                        log.info("Salvando aposta - {}", aposta);
                                        return Mono.defer(() -> repository.save(aposta))
                                                .flatMap(apostaSalva -> {
                                                    // Chamar outro método para retirar saldo do usuário
                                                    return usuarioClient.transacao(String.valueOf(usuarioDTO.usuarioId()), new TransacaoDTO(aposta.getValorApostado(), TipoTransacao.SAQUE))
                                                            .thenReturn(apostaSalva);
                                                });
                                    } else {
                                        return Mono.error(new RuntimeException("Saldo insuficiente para aposta."));//ATENCAO
                                    }
                                })))
                .subscribeOn(Schedulers.boundedElastic());
    }


    public Mono<Aposta> get(String apostaId) {
        return Mono.defer(() -> {
            log.info("Buscando jogo - {}", apostaId);
            return repository.findById(apostaId);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<Aposta> getAll() {
        return Flux.defer(() -> {
            log.info("Buscando todos as apostas");
            return repository.findAll();
        }).subscribeOn(Schedulers.boundedElastic());
    }
    public Flux<Aposta> getAllByStatus(String status) {
        return Flux.defer(() -> {
            log.info("Buscando todos as Apostas - {}", status);
            return repository.findAll().filter(aposta -> aposta.getStatus().toString().equalsIgnoreCase(status));
        }).subscribeOn(Schedulers.boundedElastic());
    }


    public Mono<Aposta> authorize(Aposta aposta) {

        return Mono.defer(() -> {
            log.info("Iniciando autorizacao de aposta");
            aposta.setStatus(Status.EM_ANDAMENTO);
            aposta.setModificacao(Instant.now());
           return repository.save(aposta);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Aposta> conclude(Aposta aposta, JogoDTO jogoDTO) {
        return Mono.defer(() -> {
            log.info("Iniciando encerramento de aposta");
            aposta.setStatus(Status.ENCERRADO);
            aposta.setModificacao(Instant.now());
            if (aposta.getTimeApostado().equalsIgnoreCase(jogoDTO.vencedor()) && aposta.getPremiacao().equals(Premiacao.INDISPONIVEL)){
                aposta.setPremiacao(Premiacao.DISPONIVEL);
            }
            return repository.save(aposta);
        }).subscribeOn(Schedulers.boundedElastic());
    }



    public Flux<Aposta> getAllByAvaliablePrize(String premiacao){
        return Flux.defer(() -> {
            log.info("Buscando todos as Apostas com premiacao - {}", premiacao);
            return repository.findAll()
                    .filter(aposta -> aposta.getPremiacao().toString().equalsIgnoreCase(premiacao));
        }).subscribeOn(Schedulers.boundedElastic());
    }
}

