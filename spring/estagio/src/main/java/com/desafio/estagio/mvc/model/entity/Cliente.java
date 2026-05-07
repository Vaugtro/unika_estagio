package com.desafio.estagio.mvc.model.entity;

import com.desafio.estagio.mvc.model.dto.TipoCliente;

import java.time.LocalDateTime;
import java.util.List;

public interface Cliente {
    void setId(Long id);
    Long getId();

    TipoCliente getTipo();

    void setTipo(TipoCliente tipo);

    String getEmail();

    void setEmail(String email);

    List<Endereco> getEnderecos();

    void setEnderecos(List<Endereco> enderecos);

    void setCreatedAt(LocalDateTime createdAt);
    LocalDateTime getCreatedAt();

    void setUpdatedAt(LocalDateTime updatedAt);
    LocalDateTime getUpdatedAt();

    Boolean getEstaAtivo();

    void setEstaAtivo(Boolean estaAtivo);
}
