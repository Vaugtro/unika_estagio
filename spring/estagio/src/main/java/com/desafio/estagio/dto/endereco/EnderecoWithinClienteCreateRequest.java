package com.desafio.estagio.dto.endereco;

import com.desafio.estagio.dto.endereco.sanitizer.CEPSanitizer;
import com.desafio.estagio.dto.endereco.sanitizer.TelefoneSanitizer;
import com.desafio.estagio.validation.annotation.ValidCEP;
import com.desafio.estagio.validation.annotation.ValidTelefone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serial;
import java.io.Serializable;

/**
 * Request DTO for creating an Endereco
 */
@Schema(name = "EnderecoWithinClienteCreateRequest", description = "Dados para criar um endereço")
public record EnderecoWithinClienteCreateRequest(
        @Schema(description = "Logradouro", example = "Rua das Flores", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Logradouro é obrigatório")
        String logradouro,

        @Schema(description = "Número", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Número é obrigatório")
        @PositiveOrZero(message = "Número deve ser positivo ou zero")
        Long numero,

        @Schema(description = "CEP", example = "01001-000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "CEP é obrigatório")
        @ValidCEP
        String cep,

        @Schema(description = "Bairro", example = "Centro", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Bairro é obrigatório")
        String bairro,

        @Schema(description = "Telefone", example = "(11) 91234-5678", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Telefone é obrigatório")
        @ValidTelefone
        String telefone,

        @Schema(description = "Estado (SP)", example = "SP", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Estado é obrigatório")
        String estado,

        @Schema(description = "Cidade", example = "São Paulo", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Cidade é obrigatório")
        String cidade,

        @Schema(description = "Indica se é o endereço principal", example = "true")
        Boolean principal,

        @Schema(description = "Complemento", example = "Apto 42")
        String complemento
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public EnderecoWithinClienteCreateRequest {
        cep = CEPSanitizer.sanitize(cep);
        telefone = TelefoneSanitizer.sanitize(telefone);
    }
}