package com.desafio.estagio.model;

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
