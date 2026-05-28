package com.desafio.estagio.dto.municipio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Schema(name = "MunicipioDTO", description = "Dados de um município")
public record MunicipioDTO(
        @Schema(description = "ID IBGE", example = "3550308") Long id,
        @Schema(description = "Nome", example = "São Paulo") String nome,
        @Schema(description = "Sigla da UF", example = "SP") String ufSigla
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
