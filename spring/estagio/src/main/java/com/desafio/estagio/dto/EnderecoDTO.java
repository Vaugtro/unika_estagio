package com.desafio.estagio.dto;

import com.desafio.estagio.validation.annotation.ValidCEP;
import com.desafio.estagio.validation.annotation.ValidTelefone;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public interface EnderecoDTO {

    @Schema(name = "EnderecoCreateRequest", description = "Dados para criar um endereço")
    record CreateRequest(
            @Schema(description = "Logradouro", example = "Rua das Flores", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "Logradouro é obrigatório")
            String logradouro,

            @Schema(description = "Número", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull(message = "Número é obrigatório")
            @PositiveOrZero(message = "Número deve ser positivo ou zero")
            Long numero,

            @Schema(description = "CEP", example = "01001-000", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "CEP é obrigatório")
            @ValidCEP
            String cep,

            @Schema(description = "Bairro", example = "Centro", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "Bairro é obrigatório")
            String bairro,

            @Schema(description = "Telefone", example = "(11) 91234-5678", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "Telefone é obrigatório")
            @ValidTelefone
            String telefone,

            @Schema(description = "Estado (SP)", example = "SP", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "Estado é obrigatório")
            String estado,

            @Schema(description = "Cidade", example = "São Paulo", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "Cidade é obrigatório")
            String cidade,

            @Schema(description = "Indica se é o endereço principal", example = "true")
            Boolean principal,

            @Schema(description = "Complemento", example = "Apto 42")
            String complemento,

            @Schema(description = "ID do cliente dono do endereço", example = "1")
            @NotNull(message = "ID do cliente é obrigatório")
            Long clienteId
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }

    @Schema(name = "EnderecoUpdateRequest", description = "Dados para atualizar um endereço")
    record UpdateRequest(
            @Schema(description = "Logradouro", example = "Rua das Flores")
            String logradouro,

            @Schema(description = "Número", example = "123")
            @PositiveOrZero(message = "Número deve ser positivo ou zero")
            Long numero,

            @Schema(description = "CEP", example = "01001-000")
            @ValidCEP
            String cep,

            @Schema(description = "Bairro", example = "Centro")
            String bairro,

            @Schema(description = "Telefone", example = "(11) 91234-5678")
            @ValidTelefone
            String telefone,

            @Schema(description = "Estado", example = "SP")
            String estado,

            @Schema(description = "Cidade", example = "São Paulo")
            String cidade,

            @Schema(description = "Endereço principal", example = "true")
            Boolean principal,

            @Schema(description = "Complemento", example = "Apto 42")
            String complemento
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    record Response(
            Long id,
            String logradouro,
            Long numero,
            String cep,
            String bairro,
            String telefone,
            String estado,
            String cidade,
            Boolean principal,
            String complemento,
            Long clienteId,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime createdAt,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime updatedAt
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }

    @Builder
    record ListResponse(
            Long id,
            String logradouro,
            Long numero,
            String cep,
            String bairro,
            String cidade,
            String estado,
            long clienteId,
            Boolean principal
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }

    @Builder
    record ReportResponse(
            Long id,
            String logradouro,
            Long numero,
            String cep,
            String bairro,
            String cidade,
            String estado,
            String telefone,
            Boolean principal,
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDateTime createdAt
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}