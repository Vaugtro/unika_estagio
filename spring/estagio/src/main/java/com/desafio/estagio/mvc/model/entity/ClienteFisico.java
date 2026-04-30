package com.desafio.estagio.mvc.model.entity;

import java.time.LocalDate;

public interface ClienteFisico extends Cliente {

    String getCpf();

    void setCpf(String cpf);

    String getNome();

    void setNome(String nome);

    String getRg();

    void setRg(String rg);

    LocalDate getDataNascimento();

    void setDataNascimento(LocalDate dataNascimento);
}
