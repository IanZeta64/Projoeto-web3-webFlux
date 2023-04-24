package br.com.ada.usuarioapi.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String usuarioId;
    @Field("name")
    private String nome;
    @Field("personalDocuments")
    private String documento;
    @Field("balance")
    private BigDecimal saldo;
}
