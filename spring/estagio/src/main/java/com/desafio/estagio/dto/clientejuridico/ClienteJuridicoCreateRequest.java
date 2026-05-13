package com.desafio.estagio.dto.clientejuridico;

import com.desafio.estagio.dto.endereco.EnderecoCreateRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CNPJ;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for creating a new ClienteJuridico
 * Only includes fields necessary for creation
 */
@Schema(name = "ClienteJuridicoCreateRequest", description = "Dados para criar um cliente pessoa jurídica")
public record ClienteJuridicoCreateRequest(
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
        List<EnderecoCreateRequest> enderecos
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}