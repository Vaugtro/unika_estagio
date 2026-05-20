package com.desafio.estagio.wicket.model;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class ClienteJuridicoUpdateFormModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String razaoSocial;
    private String cnpj;
    private String inscricaoEstadual;
    private String email;
    private LocalDate dataCriacaoEmpresa;
    private Boolean estaAtivo;

    public ClienteJuridicoUpdateFormModel() {
    }

    public ClienteJuridicoUpdateFormModel(ClienteJuridicoResponse response) {
        this.id = response.id();
        this.razaoSocial = response.razaoSocial();
        this.cnpj = response.cnpj();
        this.inscricaoEstadual = response.inscricaoEstadual();
        this.email = response.email();
        this.dataCriacaoEmpresa = response.dataCriacaoEmpresa();
        this.estaAtivo = response.estaAtivo();
    }
}
