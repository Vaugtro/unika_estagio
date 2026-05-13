package com.desafio.estagio.dto.endereco;

import com.desafio.estagio.validation.annotation.ValidCEP;
import com.desafio.estagio.validation.annotation.ValidTelefone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serial;
import java.io.Serializable;

/**
 * Request DTO for updating an Endereco
 */
@Schema(name = "EnderecoUpdateRequest", description = "Dados para atualizar um endereço")
public record EnderecoUpdateRequest(
        @Schema(description = "Logradouro", example = "Rua das Flores")
        String logradouro,

        @Schema(description = "Número", example = "123")
        @PositiveOrZero(message = "Número deve ser positivo ou zero")
        Long numero,

        @Schema(description = "CEP", example = "01001-000")
        @ValidCEP
        String cep,

        @Schema(description = "Bairro", example = "Centro")
        String bairro,

        @Schema(description = "Telefone", example = "(11) 91234-5678")
        @ValidTelefone
        String telefone,

        @Schema(description = "Estado", example = "SP")
        String estado,

        @Schema(description = "Cidade", example = "São Paulo")
        String cidade,

        @Schema(description = "Endereço principal", example = "true")
        Boolean principal,

        @Schema(description = "Complemento", example = "Apto 42")
        String complemento
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}