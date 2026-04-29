package com.desafio.estagio.mvc.model.entity;

import com.desafio.estagio.mvc.model.dto.TipoCliente;

import java.time.LocalDateTime;

interface Cliente {
    Long getId();

    TipoCliente getTipo();
    void setTipo(TipoCliente tipo);

    String getEmail();
    void setEmail(String email);

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    Boolean estaAtivo();
    void estaAtivoActivate();
    void estaAtivoDeactivate();
}
