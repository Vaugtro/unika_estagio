package com.desafio.estagio.dto;

import com.desafio.estagio.model.Cliente;
import com.desafio.estagio.validation.annotation.ValidCEP;
import com.desafio.estagio.validation.annotation.ValidTelefone;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
public interface EnderecoDTO {

    @Schema(name = "EnderecoCreateRequest")
    record CreateRequest(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @NotBlank String logradouro,

            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @PositiveOrZero Long numero,

            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @NotBlank @ValidCEP String cep,

            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @NotBlank String bairro,

            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @NotBlank @ValidTelefone String telefone,

            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @NotBlank
            String estado,

            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull @NotBlank String cidade,

            Boolean principal,

            String complemento
    ) implements Serializable {}

    @Schema(name = "EnderecoUpdateRequest")
    record UpdateRequest(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull Long id,

            String logradouro,

            @PositiveOrZero Long numero,

            @ValidCEP String cep,

            String bairro,

            @ValidTelefone String telefone,

            String estado,

            String cidade,

            Boolean principal,

            String complemento
    ) implements Serializable {}

    // Keep Response as is
    record Response(/* ... */) {}
}