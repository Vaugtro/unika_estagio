package com.desafio.estagio.mvc.model.entity;

import com.desafio.estagio.mvc.model.dto.TipoCliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cliente_juridico")
public class IClienteJuridico extends ICliente {
    @Column(name = "cnpj", unique = true, nullable = false, length = 14)
    @Getter @Setter
    private String cnpj;

    @Column(name = "razao_social", nullable = false)
    @Getter @Setter
    private String razaoSocial;

    @Column( name = "inscricao_estadual", nullable = false, length = 12)
    @Getter @Setter
    private String inscricaoEstadual;

    @Column(name = "data_criacao_empresa", nullable = false)
    @Getter @Setter
    private LocalDate dataCriacaoEmpresa;

    // --- Constructor


    public IClienteJuridico(Long id, TipoCliente tipo, String email, LocalDateTime createdAt, LocalDateTime updatedAt, Boolean estaAtivo, String cnpj, String razaoSocial, String inscricaoEstadual, LocalDate dataCriacaoEmpresa) {
        super(id, tipo, email, createdAt, updatedAt, estaAtivo);
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.inscricaoEstadual = inscricaoEstadual;
        this.dataCriacaoEmpresa = dataCriacaoEmpresa;
    }

    public IClienteJuridico() {

    }
}
