package com.desafio.estagio.model;

import com.desafio.estagio.model.enums.TipoCliente;

import java.time.LocalDateTime;
import java.util.List;

public interface Cliente {
    Long getId();

    void setId(Long id);

    TipoCliente getTipo();

    void setTipo(TipoCliente tipo);

    String getEmail();

    void setEmail(String email);

    List<Endereco> getEnderecos();

    void setEnderecos(List<Endereco> enderecos);

    void addEndereco(Endereco endereco);

    void removeEndereco(Endereco endereco);

    LocalDateTime getCreatedAt();

    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();

    void setUpdatedAt(LocalDateTime updatedAt);

    Boolean getEstaAtivo();

    void setEstaAtivo(Boolean estaAtivo);
}
