package com.desafio.estagio.dto.clientejuridico;

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
 * Request DTO for updating an existing ClienteJuridico
 * Includes all updatable fields
 */
@Schema(name = "ClienteJuridicoUpdateRequest", description = "Dados para atualizar um cliente pessoa jurídica")
public record ClienteJuridicoUpdateRequest(
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
        List<EnderecoUpdateRequest> enderecos
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}