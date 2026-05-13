package com.desafio.estagio.dto.clientefisico;

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
 * Response DTO for returning ClienteFisico data
 * Includes all fields with read-only annotations
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ClienteFisicoResponse", description = "Dados completos de um cliente pessoa física")
public record ClienteFisicoResponse(
        @Schema(description = "ID do cliente", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Tipo do cliente", example = "FISICA", accessMode = Schema.AccessMode.READ_ONLY)
        TipoCliente tipo,

        @Schema(description = "CPF (formato: 000.000.000-00)", example = "123.456.789-01")
        String cpf,

        @Schema(description = "Nome completo", example = "João Silva Santos")
        String nome,

        @Schema(description = "RG (sem formatação)", example = "123456789")
        String rg,

        @Schema(description = "E-mail do cliente", example = "joao.silva@exemplo.com", format = "email")
        String email,

        @Schema(description = "Data de nascimento", example = "1990-05-15")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataNascimento,

        @Schema(description = "Status do cliente", example = "true")
        Boolean estaAtivo,

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