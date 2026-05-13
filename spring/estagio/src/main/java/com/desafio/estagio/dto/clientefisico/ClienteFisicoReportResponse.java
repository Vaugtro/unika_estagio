package com.desafio.estagio.dto.clientefisico;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Report DTO for exporting ClienteFisico data
 * Lightweight DTO with only essential fields for reporting
 */
@Builder
@Schema(name = "ClienteFisicoReportResponse", description = "Dados de cliente pessoa física para relatório")
public record ClienteFisicoReportResponse(
        @Schema(description = "ID do cliente", example = "1")
        Long id,

        @Schema(description = "Nome completo", example = "João Silva Santos")
        String nome,

        @Schema(description = "CPF (formato: 000.000.000-00)", example = "123.456.789-01")
        String cpf,

        @Schema(description = "RG (sem formatação)", example = "123456789")
        String rg,

        @Schema(description = "E-mail do cliente", example = "joao.silva@exemplo.com")
        String email,

        @Schema(description = "Data de nascimento", example = "1990-05-15")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataNascimento,

        @Schema(description = "Status do cliente", example = "true")
        Boolean estaAtivo,

        @Schema(description = "Data de criação", example = "2026-05-05")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate createdAt
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}