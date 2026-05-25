package com.desafio.estagio.dto.clientefisico;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * Request DTO for updating an existing ClienteFisico
 * Includes all updatable fields
 */
@Builder
@Schema(name = "ClienteFisicoUpdateRequest", description = "Dados para atualizar um cliente pessoa física")
public record ClienteFisicoUpdateRequest(
        @Schema(
                description = "Nome completo",
                example = "João Silva Santos",
                minLength = 3,
                maxLength = 150
        )
        @NotBlank(message = "Nome não pode estar vazio")
        @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
        String nome,

        @Schema(
                description = "E-mail do cliente",
                example = "joao.silva@exemplo.com",
                format = "email",
                maxLength = 150
        )
        @Email(message = "E-mail inválido")
        @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
        String email,

        @Schema(description = "Status do cliente", example = "true")
        Boolean estaAtivo
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}