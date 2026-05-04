package com.desafio.estagio.mvc.model.dto;

import com.desafio.estagio.mvc.model.entity.ClienteJuridico;
import com.desafio.estagio.mvc.model.entity.Endereco;
import com.desafio.estagio.mvc.model.validation.annotation.ValidCNPJ;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ClienteJuridicoDTO extends ClienteDTO {

    /**
     * DTO for {@link ClienteJuridico} Responses
     */
    record Request(
            @NotNull TipoCliente tipo,
            @Email String email,
            @NotNull @Size(min = 14, max = 14) @NotEmpty @NotBlank @ValidCNPJ String cnpj,
            @NotNull @NotEmpty @NotBlank String razaoSocial,
            @NotNull @Size(min = 12, max = 12) @NotEmpty @NotBlank String inscricaoEstadual,
            Boolean estaAtivo,
            @NotNull @PastOrPresent LocalDate dataCriacaoEmpresa,
            @NotEmpty(message = "O cliente deve ter pelo menos um endereço")
            @Valid
            List<Endereco> enderecos
    ) implements ClienteDTO.Request, Serializable {
    }

    /**
     * DTO for {@link ClienteJuridico} Responses
     */
    record Response(Long id,
                    TipoCliente tipo,
                    String email,
                    String cnpj,
                    String razaoSocial,
                    String inscricaoEstadual,
                    Boolean estaAtivo,
                    LocalDate dataCriacaoEmpresa,
                    List<EnderecoDTO.Request> enderecos,
                    LocalDateTime createdAt,
                    LocalDateTime updatedAt
    ) implements ClienteDTO.Response, Serializable {
    }
}
