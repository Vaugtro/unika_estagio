package com.desafio.estagio.wicket.model;

import java.io.Serial;

public class ClienteJuridicoCreateFormModel extends ClienteCreateFormModel {
    @Serial
    private static final long serialVersionUID = 1L;

    private String cnpj;
    private String razaoSocial;
    private String inscricaoEstadual;
    private String dataCriacaoEmpresa;

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getDataCriacaoEmpresa() {
        return dataCriacaoEmpresa;
    }

    public void setDataCriacaoEmpresa(String dataCriacaoEmpresa) {
        this.dataCriacaoEmpresa = dataCriacaoEmpresa;
    }
}
