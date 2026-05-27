package com.desafio.estagio.dto;

import java.io.Serializable;

/**
 * Shared interface for list-response DTOs (fisico/juridico).
 * Allows {@code ClientesTablePanel<T>} to be parameterized over either type.
 */
public interface AbstractClienteListResponse extends Serializable {
    Long id();
    String email();
    Boolean estaAtivo();
}
