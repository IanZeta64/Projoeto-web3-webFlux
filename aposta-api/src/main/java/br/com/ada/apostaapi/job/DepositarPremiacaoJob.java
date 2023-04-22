package br.com.ada.apostaapi.job;

import br.com.ada.apostaapi.client.UsuarioClient;
import br.com.ada.apostaapi.model.Premiacao;
import br.com.ada.apostaapi.model.TipoTransacao;
import br.com.ada.apostaapi.model.TransacaoDTO;
import br.com.ada.apostaapi.services.ApostaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class DepositarPremiacaoJob implements InitializingBean {
    private final ApostaService service;
    private final UsuarioClient usuarioClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        var executors = Executors.newSingleThreadScheduledExecutor();
        executors.scheduleWithFixedDelay(() -> {
            Flux.defer(() -> service.getAllByAvaliablePrize("DISPONIVEL"))
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMap(aposta -> {
                        aposta.setPremiacao(Premiacao.RESGADATA);
                        return usuarioClient.transacao(String.valueOf(aposta.getUserId()), new TransacaoDTO(aposta.getValorPremiacao(), TipoTransacao.DEPOSITO));
                    })

                    .doOnNext(ApostaId -> log.info("Premiacão depositada com sucesso - {}", ApostaId))
                    .doOnComplete(() -> log.info("Todas as premiacões DISPONIVEIS depositadas con sucesso"))
                    .subscribe();
        }, 1, 45 , TimeUnit.SECONDS);
    }
}

