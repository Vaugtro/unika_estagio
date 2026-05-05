package com.desafio.estagio.mvc.model.dto;

import com.desafio.estagio.mvc.model.serializer.CPFFormatDeserializer;
import com.desafio.estagio.mvc.model.serializer.CPFFormatSerializer;
import com.desafio.estagio.mvc.model.validation.annotation.ValidRG;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ClienteFisicoDTO extends ClienteDTO {

    @Schema(name = "ClienteFisicoRequest", description = "Dados para criar/atualizar um cliente pessoa física")
    record Request(
            @Schema(description = "Tipo do cliente", example = "FISICA", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull TipoCliente tipo,

            @Schema(description = "E-mail do cliente", example = "joao.silva@exemplo.com", format = "email")
            @Email String email,

            @Schema(description = "CPF (formato: 000.000.000-00)", example = "123.456.789-01", requiredMode = Schema.RequiredMode.REQUIRED)
            @JsonProperty("cpf")
            @JsonDeserialize(using = CPFFormatDeserializer.class)
            @NotNull @CPF String cpf,

            @Schema(description = "Nome completo", example = "João Silva Santos", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @NotBlank String nome,

            @Schema(description = "RG (sem formatação)", example = "123456789", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @ValidRG String rg,

            @Schema(description = "Status do cliente", example = "true", defaultValue = "true")
            Boolean estaAtivo,

            @Schema(description = "Data de nascimento", example = "1990-05-15", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @PastOrPresent LocalDate dataNascimento,

            @Schema(description = "Lista de endereços do cliente", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotEmpty(message = "O cliente deve ter pelo menos um endereço")
            @Valid
            List<EnderecoDTO.Request> enderecos
    ) implements ClienteDTO.Request, Serializable {

        @JsonCreator
        public static Request fromJson(
                @JsonProperty("tipo") TipoCliente tipo,
                @JsonProperty("email") String email,
                @JsonProperty("cpf") String cpf,
                @JsonProperty("nome") String nome,
                @JsonProperty("rg") String rg,
                @JsonProperty("estaAtivo") Boolean estaAtivo,
                @JsonProperty("dataNascimento") LocalDate dataNascimento,
                @JsonProperty("enderecos") List<EnderecoDTO.Request> enderecos
        ) {
            // Normalize RG (remove all non-digits)
            String normalizedRg = cleanRg(rg);

            // Set default for estaAtivo
            Boolean activeStatus = estaAtivo != null ? estaAtivo : true;

            // Ensure enderecos is not null
            List<EnderecoDTO.Request> safeEnderecos = enderecos != null ? enderecos : List.of();

            // Validate age (18+ years)
            validateAge(dataNascimento);

            // Validate CPF and RG are not identical
            validateCpfNotEqualsRg(cpf, normalizedRg);

            return new Request(
                    tipo,
                    email,
                    cpf,
                    nome,
                    normalizedRg,
                    activeStatus,
                    dataNascimento,
                    safeEnderecos
            );
        }

        private static String cleanRg(String rg) {
            return rg != null ? rg.replaceAll("\\D", "") : null;
        }

        private static void validateAge(LocalDate dataNascimento) {
            if (dataNascimento != null && dataNascimento.isAfter(LocalDate.now().minusYears(18))) {
                throw new IllegalArgumentException("Cliente deve ter pelo menos 18 anos");
            }
        }

        private static void validateCpfNotEqualsRg(String cpf, String rg) {
            if (cpf != null && rg != null) {
                String cleanCpf = cpf.replaceAll("\\D", "");
                if (cleanCpf.equals(rg)) {
                    throw new IllegalArgumentException("CPF e RG não podem ser iguais");
                }
            }
        }
    }

    @Schema(name = "ClienteFisicoResponse", description = "Dados completos de um cliente pessoa física")
    record Response(
            @Schema(description = "ID do cliente", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
            Long id,

            @Schema(description = "Tipo do cliente", example = "FISICA")
            TipoCliente tipo,

            @Schema(description = "E-mail do cliente", example = "joao.silva@exemplo.com", format = "email")
            String email,

            @Schema(description = "CPF (formato: 000.000.000-00)", example = "123.456.789-01")
            @JsonSerialize(using = CPFFormatSerializer.class)
            String cpf,

            @Schema(description = "Nome completo", example = "João Silva Santos")
            String nome,

            @Schema(description = "RG (sem formatação)", example = "123456789")
            String rg,

            @Schema(description = "Status do cliente", example = "true")
            Boolean estaAtivo,

            @Schema(description = "Data de nascimento", example = "1990-05-15")
            LocalDate dataNascimento,

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