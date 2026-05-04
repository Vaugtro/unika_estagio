package com.desafio.estagio.mvc.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cliente_juridico")
public class ClienteJuridicoEntity extends ClienteEntity implements ClienteJuridico {
    @Column(name = "cnpj", unique = true, nullable = false, length = 14)
    @Getter
    @Setter
    private String cnpj;

    @Column(name = "razao_social", nullable = false)
    @Getter
    @Setter
    private String razaoSocial;

    @Column(name = "inscricao_estadual", nullable = false, length = 12)
    @Getter
    @Setter
    private String inscricaoEstadual;

    @Column(name = "data_criacao_empresa", nullable = false)
    @Getter
    @Setter
    private LocalDate dataCriacaoEmpresa;
}
