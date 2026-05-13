package com.desafio.estagio.dto.endereco;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Report DTO for exporting Endereco data
 * Lightweight DTO with only essential fields for reporting
 */
@Builder
@Schema(name = "EnderecoReportResponse", description = "Dados de endereço para relatório")
public record EnderecoReportResponse(
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

        @Schema(description = "Cidade", example = "São Paulo")
        String cidade,

        @Schema(description = "Estado", example = "SP")
        String estado,

        @Schema(description = "Telefone", example = "(11) 91234-5678")
        String telefone,

        @Schema(description = "Indica se é o endereço principal", example = "true")
        Boolean principal,

        @Schema(description = "Data de criação", example = "2026-05-05")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDateTime createdAt
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}