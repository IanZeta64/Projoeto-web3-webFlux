package br.com.ada.usuarioapi.model;

import java.math.BigDecimal;

public record Transacao(BigDecimal valorTransacao, TipoTransacao tipoTransacao) {
}
