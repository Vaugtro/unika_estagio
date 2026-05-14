package com.desafio.estagio.dto.clientefisico;

import com.desafio.estagio.dto.endereco.EnderecoWithinClienteCreateRequest;
import com.desafio.estagio.validation.annotation.ValidRG;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for creating a new ClienteFisico
 * Only includes fields necessary for creation
 */
@Schema(name = "ClienteFisicoCreateRequest", description = "Dados para criar um cliente pessoa física")
public record ClienteFisicoCreateRequest(
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
        List<EnderecoWithinClienteCreateRequest> enderecos
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public ClienteFisicoCreateRequest {
        if (cpf != null) {
            cpf = cpf.replaceAll("\\D", "");
        }
    }
}