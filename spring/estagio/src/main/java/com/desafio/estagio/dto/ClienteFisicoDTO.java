package com.desafio.estagio.dto;

import com.desafio.estagio.model.enums.TipoCliente;
import com.desafio.estagio.validation.annotation.ValidRG;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for ClienteFisico (Pessoa Física)
 * Separates Request, Response, and Report DTOs for clear separation of concerns
 */
public interface ClienteFisicoDTO extends ClienteDTO {

    /**
     * Request DTO for creating a new ClienteFisico
     * Only includes fields necessary for creation
     */
    @Schema(name = "ClienteFisicoCreateRequest", description = "Dados para criar um cliente pessoa física")
    record CreateRequest(
            @Schema(
                    description = "CPF (formato: 000.000.000-00)",
                    example = "123.456.789-01",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "CPF é obrigatório")
            @CPF(message = "CPF inválido")
            String cpf,

            @Schema(
                    description = "Nome completo",
                    example = "João Silva Santos",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    minLength = 3,
                    maxLength = 150
            )
            @NotNull(message = "Nome é obrigatório")
            @NotBlank(message = "Nome não pode estar vazio")
            @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
            String nome,

            @Schema(
                    description = "RG (sem formatação)",
                    example = "123456789",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "RG é obrigatório")
            @ValidRG(message = "RG inválido")
            String rg,

            @Schema(
                    description = "E-mail do cliente",
                    example = "joao.silva@exemplo.com",
                    format = "email",
                    maxLength = 150
            )
            @Email(message = "E-mail inválido")
            @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
            String email,

            @Schema(
                    description = "Data de nascimento",
                    example = "1990-05-15",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "Data de nascimento é obrigatória")
            @PastOrPresent(message = "Data de nascimento não pode ser no futuro")
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate dataNascimento,

            @Schema(
                    description = "Lista de endereços do cliente",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    minContains = 1
            )
            @NotEmpty(message = "O cliente deve ter pelo menos um endereço")
            @Valid
            List<EnderecoDTO.CreateRequest> enderecos
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }

    /**
     * Request DTO for updating an existing ClienteFisico
     * Includes all updatable fields
     */
    @Schema(name = "ClienteFisicoUpdateRequest", description = "Dados para atualizar um cliente pessoa física")
    record UpdateRequest(
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

            @Schema(
                    description = "Data de nascimento",
                    example = "1990-05-15"
            )
            @PastOrPresent(message = "Data de nascimento não pode ser no futuro")
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate dataNascimento,

            @Schema(
                    description = "Lista de endereços do cliente",
                    minContains = 1
            )
            @NotEmpty(message = "O cliente deve ter pelo menos um endereço")
            @Valid
            List<EnderecoDTO.UpdateRequest> enderecos
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }

    /**
     * Response DTO for returning ClienteFisico data
     * Includes all fields with read-only annotations
     */
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "ClienteFisicoResponse", description = "Dados completos de um cliente pessoa física")
    record Response(
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
                    schema = @Schema(implementation = EnderecoDTO.Response.class)
            )
            List<EnderecoDTO.Response> enderecos,

            @Schema(description = "Data de criação", example = "2026-05-05T10:30:00Z", accessMode = Schema.AccessMode.READ_ONLY)
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime createdAt,

            @Schema(description = "Data da última atualização", example = "2026-05-05T15:45:00Z", accessMode = Schema.AccessMode.READ_ONLY)
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime updatedAt
    ) implements ClienteDTO.Response, Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }

    /**
     * Report DTO for exporting ClienteFisico data
     * Lightweight DTO with only essential fields for reporting
     */
    @Builder
    @Schema(name = "ClienteFisicoReportResponse", description = "Dados de cliente pessoa física para relatório")
    record ReportResponse(
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

    /**
     * Minimal DTO for list responses
     * Used when returning multiple clients with limited data
     */
    @Builder
    @Schema(name = "ClienteFisicoListResponse", description = "Dados resumidos de cliente pessoa física para listas")
    record ListResponse(
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
}