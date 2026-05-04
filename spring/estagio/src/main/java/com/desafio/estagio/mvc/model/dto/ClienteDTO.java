package com.desafio.estagio.mvc.model.dto;

import com.desafio.estagio.mvc.model.entity.Endereco;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public interface ClienteDTO {

    // Common contract for all Requests
    interface Request extends Serializable {
        TipoCliente tipo();

        String email();

        Boolean estaAtivo();

        List<Endereco> enderecos();
    }

    // Common contract for all Responses
    // The Service uses <S extends ClienteDTO.Response>
    interface Response extends Serializable {
        Long id();

        TipoCliente tipo();

        String email();

        Boolean estaAtivo();

        List<EnderecoDTO.Response> enderecos();

        LocalDateTime createdAt();

        LocalDateTime updatedAt();
    }
}