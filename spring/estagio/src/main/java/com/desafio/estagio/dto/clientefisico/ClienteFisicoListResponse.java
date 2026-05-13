package com.desafio.estagio.dto.clientefisico;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * Minimal DTO for list responses
 * Used when returning multiple clients with limited data
 */
@Builder
@Schema(name = "ClienteFisicoListResponse", description = "Dados resumidos de cliente pessoa física para listas")
public record ClienteFisicoListResponse(
        @Schema(description = "ID do cliente", example = "1")
        Long id,

        @Schema(description = "Nome completo", example = "João Silva Santos")
        String nome,

        @Schema(description = "CPF (formato: 000.000.000-00)", example = "123.456.789-01")
        String cpf,

        @Schema(description = "E-mail do cliente", example = "joao.silva@exemplo.com")
        String email,

        @Schema(description = "Status do cliente", example = "true")
        Boolean estaAtivo
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}