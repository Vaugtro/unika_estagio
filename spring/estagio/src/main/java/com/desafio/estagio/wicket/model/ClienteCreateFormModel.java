package com.desafio.estagio.wicket.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class ClienteCreateFormModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private List<EnderecoCreateFormModel> enderecos = new ArrayList<>();

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<EnderecoCreateFormModel> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<EnderecoCreateFormModel> enderecos) {
        this.enderecos = enderecos;
    }
}
