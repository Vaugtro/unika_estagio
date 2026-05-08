package com.desafio.estagio.mvc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CNPJ;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ClienteJuridicoDTO extends ClienteDTO {

    @Schema(name = "ClienteJuridicoRequest", description = "Dados para criar/atualizar um cliente pessoa jurídica")
    record Request(
            @Schema(description = "Tipo do cliente", example = "JURIDICA", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull TipoCliente tipo,

            @Schema(description = "E-mail do cliente", example = "contato@empresa.com.br", format = "email")
            @Email String email,

            @Schema(description = "CNPJ (formato: 00.000.000/0000-00)", example = "12.345.678/0001-90", requiredMode = Schema.RequiredMode.REQUIRED)
            @JsonProperty("cnpj")
            @NotNull @CNPJ String cnpj,

            @Schema(description = "Razão Social", example = "Empresa Exemplo LTDA", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @NotBlank String razaoSocial,

            @Schema(description = "Inscrição Estadual (sem formatação)", example = "123456789", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @NotBlank String inscricaoEstadual,

            @Schema(description = "Status do cliente", example = "true", defaultValue = "true")
            Boolean estaAtivo,

            @Schema(description = "Data de criação da empresa", example = "2020-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @PastOrPresent LocalDate dataCriacaoEmpresa,

            @Schema(description = "Lista de endereços do cliente", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotEmpty(message = "O cliente deve ter pelo menos um endereço")
            @Valid
            List<EnderecoDTO.Request> enderecos
    ) implements ClienteDTO.Request, Serializable {

    }

    @Schema(name = "ClienteJuridicoResponse", description = "Dados completos de um cliente pessoa jurídica")
    record Response(
            @Schema(description = "ID do cliente", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
            Long id,

            @Schema(description = "Tipo do cliente", example = "JURIDICA")
            TipoCliente tipo,

            @Schema(description = "E-mail do cliente", example = "contato@empresa.com.br", format = "email")
            String email,

            @Schema(description = "CNPJ (formato: 00.000.000/0000-00)", example = "12.345.678/0001-90")
            String cnpj,

            @Schema(description = "Razão Social", example = "Empresa Exemplo LTDA")
            String razaoSocial,

            @Schema(description = "Inscrição Estadual (sem formatação)", example = "123456789")
            String inscricaoEstadual,

            @Schema(description = "Status do cliente", example = "true")
            Boolean estaAtivo,

            @Schema(description = "Data de criação da empresa", example = "2020-01-15")
            LocalDate dataCriacaoEmpresa,

            @ArraySchema(
                    arraySchema = @Schema(description = "Lista de endereços do cliente"),
                    schema = @Schema(implementation = EnderecoDTO.Response.class)
            )
            List<EnderecoDTO.Response> enderecos,

            @Schema(description = "Data de criação", example = "2026-05-05T10:30:00Z", accessMode = Schema.AccessMode.READ_ONLY)
            LocalDateTime createdAt,

            @Schema(description = "Data da última atualização", example = "2026-05-05T15:45:00Z", accessMode = Schema.AccessMode.READ_ONLY)
            LocalDateTime updatedAt
    ) implements ClienteDTO.Response, Serializable {
    }

    record ReportResponse(
            String nome,
            String cpf,
            String rg,
            String email,
            LocalDate dataNascimento
    ) implements Serializable {

    }
}