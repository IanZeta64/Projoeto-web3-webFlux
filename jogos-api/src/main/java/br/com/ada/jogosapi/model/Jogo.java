package br.com.ada.jogosapi.model;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
//@Document("jogo")

public class Jogo {

//    @Id
    private UUID jogoId;
    private String mandante;
    private String visitante;
    private Long golsPorMandante;
    private Long golsPorVisitante;
    private Long saldoGols;
    private Status status;
    private LocalDateTime dataHoraJogo;
    private Instant inicioPartida;
    private Instant finalPartida;
    private String vencedor;

}
