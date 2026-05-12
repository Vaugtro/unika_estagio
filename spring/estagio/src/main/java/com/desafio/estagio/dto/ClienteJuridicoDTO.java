package com.desafio.estagio.dto;

import com.desafio.estagio.model.enums.TipoCliente;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.hibernate.validator.constraints.br.CNPJ;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for ClienteJuridico (Pessoa Jurídica)
 * Separates Request, Response, and Report DTOs for clear separation of concerns
 */
public interface ClienteJuridicoDTO extends ClienteDTO {

    /**
     * Request DTO for creating a new ClienteJuridico
     * Only includes fields necessary for creation
     */
    @Schema(name = "ClienteJuridicoCreateRequest", description = "Dados para criar um cliente pessoa jurídica")
    record CreateRequest(
            @Schema(
                    description = "CNPJ (formato: 00.000.000/0000-00)",
                    example = "12.345.678/0001-90",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "CNPJ é obrigatório")
            @CNPJ(message = "CNPJ inválido")
            String cnpj,

            @Schema(
                    description = "Razão Social",
                    example = "Empresa Exemplo LTDA",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    minLength = 3,
                    maxLength = 150
            )
            @NotNull(message = "Razão Social é obrigatória")
            @NotBlank(message = "Razão Social não pode estar vazia")
            @Size(min = 3, max = 150, message = "Razão Social deve ter entre 3 e 150 caracteres")
            String razaoSocial,

            @Schema(
                    description = "Inscrição Estadual (sem formatação)",
                    example = "123456789",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "Inscrição Estadual é obrigatória")
            @NotBlank(message = "Inscrição Estadual não pode estar vazia")
            @Pattern(regexp = "^\\d+$", message = "Inscrição Estadual deve conter apenas números")
            @Size(max = 20, message = "Inscrição Estadual deve ter no máximo 20 caracteres")
            String inscricaoEstadual,

            @Schema(
                    description = "E-mail do cliente",
                    example = "contato@empresa.com.br",
                    format = "email",
                    maxLength = 150
            )
            @Email(message = "E-mail inválido")
            @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
            String email,

            @Schema(
                    description = "Data de criação da empresa",
                    example = "2020-01-15",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "Data de criação da empresa é obrigatória")
            @PastOrPresent(message = "Data de criação não pode ser no futuro")
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate dataCriacaoEmpresa,

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
     * Request DTO for updating an existing ClienteJuridico
     * Includes all updatable fields
     */
    @Schema(name = "ClienteJuridicoUpdateRequest", description = "Dados para atualizar um cliente pessoa jurídica")
    record UpdateRequest(
            @Schema(
                    description = "Razão Social",
                    example = "Empresa Exemplo LTDA",
                    minLength = 3,
                    maxLength = 150
            )
            @NotBlank(message = "Razão Social não pode estar vazia")
            @Size(min = 3, max = 150, message = "Razão Social deve ter entre 3 e 150 caracteres")
            String razaoSocial,

            @Schema(
                    description = "Inscrição Estadual",
                    example = "123456789",
                    maxLength = 20
            )
            @Pattern(regexp = "^\\d+$", message = "Inscrição Estadual deve conter apenas números")
            @Size(max = 20, message = "Inscrição Estadual deve ter no máximo 20 caracteres")
            String inscricaoEstadual,

            @Schema(
                    description = "E-mail do cliente",
                    example = "contato@empresa.com.br",
                    format = "email",
                    maxLength = 150
            )
            @Email(message = "E-mail inválido")
            @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
            String email,

            @Schema(
                    description = "Data de criação da empresa",
                    example = "2020-01-15"
            )
            @PastOrPresent(message = "Data de criação não pode ser no futuro")
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate dataCriacaoEmpresa,

            @Schema(
                    description = "Status do cliente",
                    example = "true"
            )
            Boolean estaAtivo,

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
     * Response DTO for returning ClienteJuridico data
     * Includes all fields with read-only annotations
     */
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "ClienteJuridicoResponse", description = "Dados completos de um cliente pessoa jurídica")
    record Response(
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
     * Report DTO for exporting ClienteJuridico data
     * Lightweight DTO with only essential fields for reporting
     */
    @Builder
    @Schema(name = "ClienteJuridicoReportResponse", description = "Dados de cliente pessoa jurídica para relatório")
    record ReportResponse(
            @Schema(description = "ID do cliente", example = "1")
            Long id,

            @Schema(description = "Tipo do cliente (FISICA ou JURIDICA)", example = "FISICA")
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
    ) implements Serializable, ClienteDTO.ReportResponse {
        @Serial
        private static final long serialVersionUID = 1L;
    }

    /**
     * Minimal DTO for list responses
     * Used when returning multiple clients with limited data
     */
    @Builder
    @Schema(name = "ClienteJuridicoListResponse", description = "Dados resumidos de cliente pessoa jurídica para listas")
    record ListResponse(
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
}