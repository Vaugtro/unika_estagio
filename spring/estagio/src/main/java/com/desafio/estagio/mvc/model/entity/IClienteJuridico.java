package com.desafio.estagio.mvc.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class IClienteJuridico extends ICliente implements ClienteJuridico {
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
