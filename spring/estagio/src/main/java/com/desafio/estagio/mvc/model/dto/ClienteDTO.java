package com.desafio.estagio.mvc.model.dto;

import com.desafio.estagio.mvc.model.entity.Endereco;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public interface ClienteDTO {

    /**
     * Common contract for all Requests
     */
    @Schema(name = "ClienteRequest", description = "Dados base para criação/atualização de qualquer cliente")
    interface Request extends Serializable {

        @Schema(description = "Tipo do cliente (FISICA ou JURIDICA)",
                example = "FISICA",
                requiredMode = Schema.RequiredMode.REQUIRED)
        TipoCliente tipo();

        @Schema(description = "E-mail do cliente",
                example = "cliente@exemplo.com",
                format = "email")
        String email();

        @Schema(description = "Indica se o cliente está ativo no sistema",
                example = "true",
                defaultValue = "true")
        Boolean estaAtivo();

        @ArraySchema(
                arraySchema = @Schema(description = "Lista de endereços do cliente"),
                schema = @Schema(implementation = EnderecoDTO.Request.class)
        )
        List<EnderecoDTO.Request> enderecos();
    }

    /**
     * Common contract for all Responses
     * The Service uses <S extends ClienteDTO.Response>
     */
    @Schema(name = "ClienteResponse", description = "Dados base de resposta para qualquer cliente")
    interface Response extends Serializable {

        @Schema(description = "ID único do cliente",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        Long id();

        @Schema(description = "Tipo do cliente (FISICA ou JURIDICA)",
                example = "FISICA")
        TipoCliente tipo();

        @Schema(description = "E-mail do cliente",
                example = "cliente@exemplo.com",
                format = "email")
        String email();

        @Schema(description = "Indica se o cliente está ativo no sistema",
                example = "true")
        Boolean estaAtivo();

        @ArraySchema(
                arraySchema = @Schema(description = "Lista de endereços do cliente"),
                schema = @Schema(implementation = EnderecoDTO.Response.class)
        )
        List<EnderecoDTO.Response> enderecos();

        @Schema(description = "Data de criação do registro",
                example = "2026-05-05T10:30:00Z",
                accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime createdAt();

        @Schema(description = "Data da última atualização do registro",
                example = "2026-05-05T15:45:00Z",
                accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime updatedAt();
    }
}