package com.desafio.estagio.mvc.model.entity;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link EnderecoEntity}
 */
@Value
public class EnderecoEntityDto implements Serializable {
    Long id;
    String logradouro;
    @PositiveOrZero
    Long numero;
    String cep;
    String bairro;
    String telefone;
    String cidade;
    String estado;
    Boolean eEnderecoPrincipal;
    String complemento;
    Cliente cliente;
}