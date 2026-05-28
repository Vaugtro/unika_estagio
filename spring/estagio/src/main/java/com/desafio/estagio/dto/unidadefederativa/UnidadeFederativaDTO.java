package com.desafio.estagio.dto.unidadefederativa;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Schema(name = "UnidadeFederativaDTO", description = "Dados de uma unidade federativa")
public record UnidadeFederativaDTO(
        @Schema(description = "ID IBGE", example = "35") Long id,
        @Schema(description = "Sigla", example = "SP") String sigla,
        @Schema(description = "Nome", example = "São Paulo") String nome
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
