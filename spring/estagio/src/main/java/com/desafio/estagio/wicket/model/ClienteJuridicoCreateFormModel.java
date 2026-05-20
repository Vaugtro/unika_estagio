package com.desafio.estagio.wicket.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClienteJuridicoCreateFormModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String cnpj;
    private String razaoSocial;
    private String inscricaoEstadual;
    private String email;
    private String dataCriacaoEmpresa;
    private List<EnderecoCreateFormModel> enderecos = new ArrayList<>();
}
