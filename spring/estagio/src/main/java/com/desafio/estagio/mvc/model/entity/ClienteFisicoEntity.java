package com.desafio.estagio.mvc.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="cliente_fisico")
public class ClienteFisicoEntity extends ClienteEntity implements ClienteFisico {
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
