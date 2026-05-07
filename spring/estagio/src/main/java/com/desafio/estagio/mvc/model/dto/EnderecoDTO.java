package com.desafio.estagio.mvc.model.dto;

import com.desafio.estagio.mvc.model.entity.Cliente;
import com.desafio.estagio.mvc.model.validation.annotation.ValidCEP;
import com.desafio.estagio.mvc.model.validation.annotation.ValidTelefone;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface EnderecoDTO {

    @Schema(name = "EnderecoRequest", description = "Dados para criar/atualizar um endereço")
    record Request(
            @Schema(description = "Nome da rua/avenida", example = "Rua das Flores", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @NotBlank String logradouro,

            @Schema(description = "Número do endereço", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @PositiveOrZero Long numero,

            @Schema(description = "CEP (formato: 00000-000)", example = "01234-567", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @NotBlank @ValidCEP String cep,

            @Schema(description = "Bairro", example = "Centro", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @NotBlank String bairro,

            @Schema(description = "Telefone (formato: (00) 00000-0000 ou (00) 0000-0000)",
                    example = "(11) 91234-5678", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @NotBlank @ValidTelefone String telefone,

            @Schema(description = "Cidade", example = "São Paulo", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @NotBlank String cidade,

            @Schema(description = "Estado (sigla)", example = "SP", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @NotBlank String estado,

            @Schema(description = "Indica se é o endereço principal do cliente", example = "true", defaultValue = "false")
            Boolean principal,

            @Schema(description = "Complemento (apartamento, sala, bloco, etc.)", example = "Apto 42", nullable = true)
            String complemento,

            @Schema(description = "Cliente associado ao endereço", hidden = true)
            @JsonIgnore
            Cliente cliente
    ) implements Serializable {
    }

    @Schema(name = "EnderecoResponse", description = "Dados completos do endereço")
    record Response(
            @Schema(description = "ID do endereço", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
            Long id,

            @Schema(description = "Nome da rua/avenida", example = "Rua das Flores")
            String logradouro,

            @Schema(description = "Número do endereço", example = "123")
            Long numero,

            @Schema(description = "CEP (formato: 00000-000)", example = "01234-567")
            String cep,

            @Schema(description = "Bairro", example = "Centro")
            String bairro,

            @Schema(description = "Telefone (formato: (00) 00000-0000 ou (00) 0000-0000)",
                    example = "(11) 91234-5678")

            String telefone,

            @Schema(description = "Cidade", example = "São Paulo")
            String cidade,

            @Schema(description = "Estado (sigla)", example = "SP")
            String estado,

            @Schema(description = "Complemento", example = "Apto 42", nullable = true)
            String complemento,

            @Schema(description = "Indica se é o endereço principal", example = "true")
            Boolean principal,

            @Schema(description = "Cliente associado", hidden = true)
            @JsonIgnore
            Cliente cliente,

            @Schema(description = "Data de criação", example = "2026-05-05T10:30:00Z", accessMode = Schema.AccessMode.READ_ONLY)
            LocalDateTime createdAt,

            @Schema(description = "Data da última atualização", example = "2026-05-05T15:45:00Z", accessMode = Schema.AccessMode.READ_ONLY)
            LocalDateTime updatedAt
    ) implements Serializable {
    }
}