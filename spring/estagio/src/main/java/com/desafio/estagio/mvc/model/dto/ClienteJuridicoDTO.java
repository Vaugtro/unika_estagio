package com.desafio.estagio.mvc.model.dto;

import com.desafio.estagio.mvc.model.serializer.CNPJFormatDeserializer;
import com.desafio.estagio.mvc.model.serializer.CNPJFormatSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
            @JsonDeserialize(using = CNPJFormatDeserializer.class)
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

        @JsonCreator
        public static Request fromJson(
                @JsonProperty("tipo") TipoCliente tipo,
                @JsonProperty("email") String email,
                @JsonProperty("cnpj") String cnpj,
                @JsonProperty("razaoSocial") String razaoSocial,
                @JsonProperty("inscricaoEstadual") String inscricaoEstadual,
                @JsonProperty("estaAtivo") Boolean estaAtivo,
                @JsonProperty("dataCriacaoEmpresa") LocalDate dataCriacaoEmpresa,
                @JsonProperty("enderecos") List<EnderecoDTO.Request> enderecos
        ) {
            // Normalize Inscrição Estadual (remove all non-digits)
            String normalizedIE = cleanInscricaoEstadual(inscricaoEstadual);

            // Set default for estaAtivo
            Boolean activeStatus = estaAtivo != null ? estaAtivo : true;

            // Ensure enderecos is not null
            List<EnderecoDTO.Request> safeEnderecos = enderecos != null ? enderecos : List.of();

            // Validate company age (minimum 1 year)
            validateCompanyAge(dataCriacaoEmpresa);

            // Validate IE length after cleaning
            validateInscricaoEstadualLength(normalizedIE);

            return new Request(
                    tipo,
                    email,
                    cnpj,
                    razaoSocial,
                    normalizedIE,
                    activeStatus,
                    dataCriacaoEmpresa,
                    safeEnderecos
            );
        }

        private static String cleanInscricaoEstadual(String ie) {
            if (ie == null) return null;
            return ie.replaceAll("\\D", "");
        }

        private static void validateCompanyAge(LocalDate dataCriacaoEmpresa) {
            if (dataCriacaoEmpresa != null && dataCriacaoEmpresa.isAfter(LocalDate.now().minusYears(1))) {
                throw new IllegalArgumentException("Empresa deve ter pelo menos 1 ano de existência");
            }
        }

        private static void validateInscricaoEstadualLength(String ie) {
            if (ie != null && (ie.length() < 8 || ie.length() > 14)) {
                throw new IllegalArgumentException("Inscrição Estadual deve ter entre 8 e 14 dígitos");
            }
        }
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
            @JsonSerialize(using = CNPJFormatSerializer.class)
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
}