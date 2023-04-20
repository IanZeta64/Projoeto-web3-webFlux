package br.com.ada.apostaapi.client.dto;

import br.com.ada.apostaapi.model.Status;

import java.util.UUID;

public record JogoDTO(UUID jogoId, String mandante, String visitante, Status status, Long saldoGols, String vencedor) {
}
