package com.desafio.estagio.mvc.model.entity;

import java.time.LocalDate;

public interface ClienteJuridico extends Cliente {

    String getCnpj();

    void setCnpj(String cnpj);

    String getRazaoSocial();

    void setRazaoSocial(String razaoSocial);

    String getInscricaoEstadual();

    void setInscricaoEstadual(String inscricaoEstadual);

    LocalDate getDataCriacaoEmpresa();

    void setDataCriacaoEmpresa(LocalDate dataCriacaoEmpresa);
}
