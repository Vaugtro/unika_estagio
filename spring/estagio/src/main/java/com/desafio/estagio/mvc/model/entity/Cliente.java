package com.desafio.estagio.mvc.model.entity;

import com.desafio.estagio.mvc.model.dto.TipoCliente;

import java.time.LocalDateTime;
import java.util.List;

public interface Cliente {
    Long getId();

    TipoCliente getTipo();
    void setTipo(TipoCliente tipo);

    String getEmail();
    void setEmail(String email);

    void setEnderecos(List<Endereco> enderecos);
    List<Endereco> getEnderecos();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    void setEstaAtivo(Boolean estaAtivo);
    Boolean getEstaAtivo();
}
