package com.desafio.estagio.dto;

import com.desafio.estagio.model.enums.TipoCliente;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Base DTO interface for Cliente entities
 * Defines common contracts for Request and Response DTOs
 *
 * This is a marker interface used for generic type binding in the service layer
 * Actual implementations are in ClienteFisicoDTO and ClienteJuridicoDTO
 */
public interface ClienteDTO {

    // ========== Request Hierarchy ==========

    /**
     * Base contract for all Request DTOs (Create and Update)
     *
     * NOTE: This is intentionally minimal - it only defines the most basic fields
     * that are common to ALL tipos (FISICA and JURIDICA).
     *
     * Specific request types (CreateRequest, UpdateRequest) should NOT extend this.
     * They should be defined in concrete implementations (ClienteFisicoDTO, ClienteJuridicoDTO)
     */
    @Schema(name = "ClienteRequest", description = "Contrato base para requisições de cliente")
    interface Request extends Serializable {
        // This is a marker interface for validation purposes
        // Actual fields are defined in concrete implementations
    }

    /**
     * Specific Request for CREATE operations
     * Contains all required fields for creating a new client
     */
    @Schema(name = "ClienteCreateRequest", description = "Dados para criar um novo cliente")
    interface CreateRequest extends Request, Serializable {
        Long id();
        @Schema(description = "Tipo do cliente", example = "FISICA", accessMode = Schema.AccessMode.READ_ONLY)
        TipoCliente tipo();
        LocalDateTime createdAt();
        LocalDateTime updatedAt();
    }

    /**
     * Specific Request for UPDATE operations
     * Contains only updatable fields (not identity fields)
     */
    @Schema(name = "ClienteUpdateRequest", description = "Dados para atualizar um cliente")
    interface UpdateRequest extends Request, Serializable {
        // Marker interface - implementations define specific updatable fields
    }

    // ========== Response Hierarchy ==========

    /**
     * Base contract for all Response DTOs
     *
     * Contains fields common to ALL tipos (FISICA and JURIDICA)
     * Read-only fields are marked with accessMode = READ_ONLY
     */
    @Schema(name = "ClienteResponse", description = "Contrato base para respostas de cliente")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    interface Response extends Serializable {

        /**
         * Unique identifier - read-only
         */
        @Schema(
                description = "ID único do cliente",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Long id();

        /**
         * Client type (FISICA or JURIDICA) - immutable
         */
        @Schema(
                description = "Tipo do cliente (FISICA ou JURIDICA)",
                example = "FISICA",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        TipoCliente tipo();

        /**
         * Email address - updatable
         */
        @Schema(
                description = "E-mail do cliente",
                example = "cliente@exemplo.com",
                format = "email"
        )
        String email();

        /**
         * Active status - updatable
         */
        @Schema(
                description = "Indica se o cliente está ativo no sistema",
                example = "true"
        )
        Boolean estaAtivo();

        /**
         * Associated addresses - updatable
         */
        @ArraySchema(
                arraySchema = @Schema(description = "Lista de endereços do cliente"),
                schema = @Schema(implementation = EnderecoDTO.Response.class)
        )
        List<EnderecoDTO.Response> enderecos();

        /**
         * Creation timestamp - read-only
         */
        @Schema(
                description = "Data de criação do registro",
                example = "2026-05-05T10:30:00Z",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt();

        /**
         * Last update timestamp - read-only
         */
        @Schema(
                description = "Data da última atualização do registro",
                example = "2026-05-05T15:45:00Z",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt();
    }

    /**
     * Report Response DTO
     * Lightweight version with only essential fields for reporting/exporting
     */
    @Schema(name = "ClienteReportResponse", description = "Dados simplificados de cliente para relatório")
    interface ReportResponse extends Serializable {

        @Schema(description = "ID único do cliente", example = "1")
        Long id();

        @Schema(description = "Tipo do cliente (FISICA ou JURIDICA)", example = "FISICA")
        TipoCliente tipo();

        @Schema(description = "E-mail do cliente", example = "cliente@exemplo.com")
        String email();

        @Schema(description = "Status do cliente", example = "true")
        Boolean estaAtivo();

        @Schema(description = "Data de criação", example = "2026-05-05")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDateTime createdAt();
    }

    /**
     * List Response DTO
     * Minimal version for list/pagination endpoints
     */
    @Schema(name = "ClienteListResponse", description = "Dados resumidos de cliente para listas")
    interface ListResponse extends Serializable {

        @Schema(description = "ID único do cliente", example = "1")
        Long id();

        @Schema(description = "Tipo do cliente (FISICA ou JURIDICA)", example = "FISICA", accessMode = Schema.AccessMode.READ_ONLY)
        TipoCliente tipo();

        @Schema(description = "E-mail do cliente", example = "cliente@exemplo.com")
        String email();

        @Schema(description = "Status do cliente", example = "true")
        Boolean estaAtivo();
    }
}