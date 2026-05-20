package com.desafio.estagio.dto.clientefisico;

import com.desafio.estagio.dto.endereco.EnderecoUpdateRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for updating an existing ClienteFisico
 * Includes all updatable fields
 */
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