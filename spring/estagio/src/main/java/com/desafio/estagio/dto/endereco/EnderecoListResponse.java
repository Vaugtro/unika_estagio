package com.desafio.estagio.dto.endereco;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * Minimal DTO for list responses
 * Used when returning multiple enderecos with limited data
 */
@Builder
@Schema(name = "EnderecoListResponse", description = "Dados resumidos de endereço para listas")
public record EnderecoListResponse(
        @Schema(description = "ID do endereço", example = "1")
        Long id,

        @Schema(description = "Logradouro", example = "Rua das Flores")
        String logradouro,

        @Schema(description = "Número", example = "123")
        Long numero,

        @Schema(description = "CEP", example = "01001-000")
        String cep,

        @Schema(description = "Bairro", example = "Centro")
        String bairro,

        @Schema(description = "Cidade", example = "São Paulo")
        String cidade,

        @Schema(description = "Estado", example = "SP")
        String estado,

        @Schema(description = "ID do cliente dono do endereço", example = "1")
        Long clienteId,

        @Schema(description = "Indica se é o endereço principal", example = "true")
        Boolean principal
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}