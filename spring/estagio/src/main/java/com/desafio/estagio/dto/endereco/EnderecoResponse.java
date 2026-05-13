package com.desafio.estagio.dto.endereco;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Response DTO for returning Endereco data
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "EnderecoResponse", description = "Dados completos de um endereço")
public record EnderecoResponse(
        @Schema(description = "ID do endereço", example = "1")
        Long id,

        @Schema(description = "Logradouro", example = "Rua das Flores")
        String logradouro,

        @Schema(description = "Número", example = "123")
        Long numero,

        @Schema(description = "CEP", example = "01001-000")
        String cep,

        @Schema(description = "Bairro", example = "Centro")
        String bairro,

        @Schema(description = "Telefone", example = "(11) 91234-5678")
        String telefone,

        @Schema(description = "Estado", example = "SP")
        String estado,

        @Schema(description = "Cidade", example = "São Paulo")
        String cidade,

        @Schema(description = "Indica se é o endereço principal", example = "true")
        Boolean principal,

        @Schema(description = "Complemento", example = "Apto 42")
        String complemento,

        @Schema(description = "ID do cliente dono do endereço", example = "1")
        Long clienteId,

        @Schema(description = "Data de criação", example = "2026-05-05T10:30:00Z")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        @Schema(description = "Data da última atualização", example = "2026-05-05T15:45:00Z")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}