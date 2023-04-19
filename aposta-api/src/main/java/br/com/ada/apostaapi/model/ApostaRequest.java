package br.com.ada.apostaapi.model;

import java.math.BigDecimal;

public record ApostaRequest(String userId, String jogoId, BigDecimal valorAposta, String time) {
}
