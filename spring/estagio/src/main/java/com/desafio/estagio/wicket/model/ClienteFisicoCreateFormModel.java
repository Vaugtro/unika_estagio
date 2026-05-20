package com.desafio.estagio.wicket.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClienteFisicoCreateFormModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String cpf;
    private String nome;
    private String rg;
    private String email;
    private String dataNascimento;
    private List<EnderecoCreateFormModel> enderecos = new ArrayList<>();
}
