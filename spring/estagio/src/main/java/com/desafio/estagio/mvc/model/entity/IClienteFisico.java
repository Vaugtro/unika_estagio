package com.desafio.estagio.mvc.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class IClienteFisico extends ICliente implements ClienteFisico {
    @Column(name = "cpf", unique = true, nullable = false, length = 11)
    @Getter
    @Setter
    private String cpf;

    @Column(name = "nome", nullable = false)
    @Getter
    @Setter
    private String nome;

    @Column(name = "rg", nullable = false, length = 9)
    @Getter
    @Setter
    private String rg;

    @Column(name = "data_nascimento", nullable = false)
    @Getter
    @Setter
    private LocalDate dataNascimento;

}
