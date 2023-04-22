package br.com.ada.apostaapi.model;

import java.math.BigDecimal;

public record TransacaoDTO(BigDecimal valorTransacao, TipoTransacao tipoTransacao) {
}
