package com.desafio.estagio.wicket.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class EnderecoCreateFormModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String logradouro;
    private Long numero;
    private String cep;
    private String bairro;
    private String telefone;
    private String estado;
    private String cidade;
    private Boolean principal;
    private String complemento;
}
