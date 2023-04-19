package br.com.ada.apostaapi.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class Aposta {
    private UUID apostaId;
    private UUID userId;
    private UUID jogoId;
    private BigDecimal coefieciente;
    private BigDecimal valorAposta;
    private String time;
    private Status status;
    private Instant criacao;
    private Instant modificacao;
    private Boolean receber;

}
