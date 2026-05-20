package com.desafio.estagio.wicket.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class ClienteFisicoCreateFormModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String cpf;

    @NotNull(message = "Nome é obrigatório")
    @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
    private String nome;

    @NotNull(message = "RG é obrigatório")
    private String rg;

    @Email(message = "E-mail inválido")
    @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
    private String email;

    private LocalDate dataNascimento;
}
