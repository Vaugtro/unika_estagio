package com.desafio.estagio.mvc.model.dto;

import com.desafio.estagio.mvc.model.entity.Endereco;
import com.desafio.estagio.mvc.model.validation.annotation.ValidCEP;
import com.desafio.estagio.mvc.model.validation.annotation.ValidTelefone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface EnderecoDTO {

    /**
     * DTO for {@link Endereco} Requests
     */
    record Request(
            @NotNull @NotBlank String logradouro,
            @NotNull @PositiveOrZero Long numero,
            @NotNull @NotBlank @ValidCEP String cep,
            @NotNull @NotBlank String bairro,
            @NotNull @NotBlank @ValidTelefone String telefone,
            @NotNull @NotBlank String cidade,
            @NotNull @NotBlank String estado,
            Boolean principal,
            String complemento
    ) implements Serializable {
    }

    /**
     * DTO for {@link Endereco} Responses
     */
    record Response(Long id,
                    String logradouro,
                    Long numero,
                    String cep,
                    String bairro,
                    String telefone,
                    String cidade,
                    String estado,
                    String complemento,
                    Boolean principal,
                    LocalDateTime createdAt,
                    LocalDateTime updatedAt
    ) implements Serializable {
    }
}