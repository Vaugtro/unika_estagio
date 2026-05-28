package com.desafio.estagio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "UnidadeFederativa")
@Table(name = "unidade_federativa")
public class UnidadeFederativa {

    @Id
    @Column(name = "sigla", length = 2, nullable = false)
    private String sigla;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;
}
