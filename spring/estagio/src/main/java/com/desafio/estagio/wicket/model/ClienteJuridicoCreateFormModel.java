package com.desafio.estagio.wicket.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class ClienteJuridicoCreateFormModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "CNPJ é obrigatório")
    @CNPJ(message = "CNPJ inválido")
    private String cnpj;

    @NotNull(message = "Razão Social é obrigatória")
    @Size(min = 3, max = 150, message = "Razão Social deve ter entre 3 e 150 caracteres")
    private String razaoSocial;

    @NotNull(message = "Inscrição Estadual é obrigatória")
    private String inscricaoEstadual;

    @Email(message = "E-mail inválido")
    @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
    private String email;

    private LocalDate dataCriacaoEmpresa;
}
