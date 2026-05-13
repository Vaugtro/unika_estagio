package com.desafio.estagio.dto.clientejuridico;

import com.desafio.estagio.dto.endereco.EnderecoResponse;
import com.desafio.estagio.model.enums.TipoCliente;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for returning ClienteJuridico data
 * Includes all fields with read-only annotations
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ClienteJuridicoResponse", description = "Dados completos de um cliente pessoa jurídica")
public record ClienteJuridicoResponse(
        @Schema(description = "ID do cliente", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Tipo do cliente", example = "JURIDICA", accessMode = Schema.AccessMode.READ_ONLY)
        TipoCliente tipo,

        @Schema(description = "CNPJ (formato: 00.000.000/0000-00)", example = "12.345.678/0001-90")
        String cnpj,

        @Schema(description = "Razão Social", example = "Empresa Exemplo LTDA")
        String razaoSocial,

        @Schema(description = "Inscrição Estadual", example = "123456789")
        String inscricaoEstadual,

        @Schema(description = "E-mail do cliente", example = "contato@empresa.com.br", format = "email")
        String email,

        @Schema(description = "Status do cliente", example = "true")
        Boolean estaAtivo,

        @Schema(description = "Data de criação da empresa", example = "2020-01-15")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataCriacaoEmpresa,

        @ArraySchema(
                arraySchema = @Schema(description = "Lista de endereços do cliente"),
                schema = @Schema(implementation = EnderecoResponse.class)
        )
        List<EnderecoResponse> enderecos,

        @Schema(description = "Data de criação", example = "2026-05-05T10:30:00Z", accessMode = Schema.AccessMode.READ_ONLY)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        @Schema(description = "Data da última atualização", example = "2026-05-05T15:45:00Z", accessMode = Schema.AccessMode.READ_ONLY)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}