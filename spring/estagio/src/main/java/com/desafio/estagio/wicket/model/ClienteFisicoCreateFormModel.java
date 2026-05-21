package com.desafio.estagio.wicket.model;

import java.io.Serial;

public class ClienteFisicoCreateFormModel extends ClienteCreateFormModel {
    @Serial
    private static final long serialVersionUID = 1L;

    private String cpf;
    private String nome;
    private String rg;
    private String dataNascimento;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
}
