package br.com.ada.usuarioapi.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
public class Usuario {
    private UUID usuarioId;
    private String nome;
    private String documento;
    private BigDecimal saldo;
}
