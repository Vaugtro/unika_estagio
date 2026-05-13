package com.desafio.estagio.dto.clientejuridico;

import com.desafio.estagio.model.enums.TipoCliente;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Report DTO for exporting ClienteJuridico data
 * Lightweight DTO with only essential fields for reporting
 */
@Builder
@Schema(name = "ClienteJuridicoReportResponse", description = "Dados de cliente pessoa jurídica para relatório")
public record ClienteJuridicoReportResponse(
        @Schema(description = "ID do cliente", example = "1")
        Long id,

        @Schema(description = "Tipo do cliente", example = "JURIDICA")
        TipoCliente tipo,

        @Schema(description = "Razão Social", example = "Empresa Exemplo LTDA")
        String razaoSocial,

        @Schema(description = "CNPJ", example = "12.345.678/0001-90")
        String cnpj,

        @Schema(description = "Inscrição Estadual", example = "123456789")
        String inscricaoEstadual,

        @Schema(description = "E-mail do cliente", example = "contato@empresa.com.br")
        String email,

        @Schema(description = "Status do cliente", example = "true")
        Boolean estaAtivo,

        @Schema(description = "Data de criação da empresa", example = "2020-01-15")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataCriacaoEmpresa,

        @Schema(description = "Data de criação do registro", example = "2026-05-05")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDateTime createdAt
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}