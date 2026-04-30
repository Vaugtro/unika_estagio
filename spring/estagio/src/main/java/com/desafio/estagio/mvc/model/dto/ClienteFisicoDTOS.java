package com.desafio.estagio.mvc.model.dto;

import com.desafio.estagio.mvc.model.entity.ClienteFisico;
import com.desafio.estagio.mvc.model.entity.IClienteFisico;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ClienteFisicoDTOS {
    /**
     * DTO for {@link ClienteFisico} Requests
     */
    public record Request (
            String tipo,
            @Email String email,
            @NotNull @Size(min = 11, max = 11) @NotEmpty @NotBlank String cpf,
            @NotNull @NotEmpty @NotBlank String nome,
            @NotNull @Size(min = 9, max = 9) @NotEmpty @NotBlank String rg,
            @NotNull @PastOrPresent LocalDate dataNascimento) implements Serializable {
    }

    /**
     * DTO for {@link IClienteFisico} Responses
     */
    public record Response(Long id,
            TipoCliente tipo,
            @Email String email,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String cpf,
            String nome,
            String rg,
            LocalDate dataNascimento
    ) implements Serializable {
    }

}