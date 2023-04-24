package br.com.ada.jogosapi.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@Data
@Document("jogos")

public class Jogo {

    @Id
    private String jogoId;
    @Field("mandante")
    private String mandante;
    @Field("visitante")
    private String visitante;
    @Field("golsPorMandante")
    private Long golsPorMandante;
    @Field("golsPorVisitante")
    private Long golsPorVisitante;
    @Field("saldoGols")
    private Long saldoGols;
    @Field("status")
    private Status status;
    @Field("dataHoraJogo")
    private LocalDateTime dataHoraJogo;
    @Field("inicioPartida")
    private Instant inicioPartida;
    @Field("finalPartida")
    private Instant finalPartida;
    @Field("vencedor")
    private String vencedor;

}

