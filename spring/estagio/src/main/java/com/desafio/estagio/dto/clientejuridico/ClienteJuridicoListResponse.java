package com.desafio.estagio.dto.clientejuridico;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * Minimal DTO for list responses
 * Used when returning multiple clients with limited data
 */
@Builder
@Schema(name = "ClienteJuridicoListResponse", description = "Dados resumidos de cliente pessoa jurídica para listas")
public record ClienteJuridicoListResponse(
        @Schema(description = "ID do cliente", example = "1")
        Long id,

        @Schema(description = "Razão Social", example = "Empresa Exemplo LTDA")
        String razaoSocial,

        @Schema(description = "CNPJ", example = "12.345.678/0001-90")
        String cnpj,

        @Schema(description = "E-mail do cliente", example = "contato@empresa.com.br")
        String email,

        @Schema(description = "Status do cliente", example = "true")
        Boolean estaAtivo
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}