package br.com.ada.apostaapi.job;

import br.com.ada.apostaapi.model.Aposta;
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
public class AutorizarApostaJob implements InitializingBean {
    private final ApostaService service;
    @Override
    public void afterPropertiesSet() throws Exception {
        var executors = Executors.newSingleThreadScheduledExecutor();
        executors.scheduleWithFixedDelay(() -> {
            Flux.defer(() -> service.getAllByStatus("NAO_INICIADO"))
                    .subscribeOn(Schedulers.boundedElastic())
                    .map(Aposta::getApostaId)
                    .flatMap(apostaId -> service.updateStatus(String.valueOf(apostaId), "EM_ANDAMENTO") )
                    .doOnNext(ApostaId -> log.info("Aposta validada - {}", ApostaId))
                    .doOnComplete(() -> log.info("Todos as apostas em PENDENTE atualizados com sucesso!"))
                    .subscribe();
        }, 1, 45 , TimeUnit.SECONDS);
    }
}
