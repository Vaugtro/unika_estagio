package com.desafio.estagio.mvc.model.dto;

import com.desafio.estagio.mvc.model.entity.ClienteFisico;
import com.desafio.estagio.mvc.model.entity.Endereco;
import com.desafio.estagio.mvc.model.validation.annotation.ValidCPF;
import com.desafio.estagio.mvc.model.validation.annotation.ValidRG;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ClienteFisicoDTO extends ClienteDTO {

    /**
     * DTO for {@link ClienteFisico} Responses
     */
    record Request(
            @NotNull TipoCliente tipo,
            @Email String email,
            @NotNull @Size(min = 11, max = 11) @NotEmpty @NotBlank @ValidCPF String cpf,
            @NotNull @NotEmpty @NotBlank String nome,
            @NotNull @Size(min = 9, max = 9) @NotEmpty @NotBlank @ValidRG String rg,
            Boolean estaAtivo,
            @NotNull @PastOrPresent LocalDate dataNascimento,
            @NotEmpty(message = "O cliente deve ter pelo menos um endereço")
            @Valid
            List<Endereco> enderecos
    ) implements ClienteDTO.Request, Serializable {
    }

    /**
     * DTO for {@link ClienteFisico} Responses
     */
    record Response(Long id,
                    TipoCliente tipo,
                    @Email String email,
                    String cpf,
                    String nome,
                    String rg,
                    Boolean estaAtivo,
                    LocalDate dataNascimento,
                    List<EnderecoDTO.Request> enderecos,
                    LocalDateTime createdAt,
                    LocalDateTime updatedAt
    ) implements ClienteDTO.Response, Serializable {
    }
}
