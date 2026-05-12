package com.desafio.estagio.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "ClienteJuridico")
@SuperBuilder
@Table(name = "cliente_juridico")
public class ClienteJuridico extends Cliente {

    // Manual getters and setters with cleaning
    @Column(name = "cnpj", unique = true, nullable = false, length = 14)
    private String cnpj;  // Manual setter for cleaning

    @Column(name = "razao_social", nullable = false)
    private String razaoSocial;

    @Column(name = "inscricao_estadual", nullable = false, length = 14)
    private String inscricaoEstadual;  // Manual setter for cleaning

    @Column(name = "data_criacao_empresa", nullable = false)
    private LocalDate dataCriacaoEmpresa;

    public void setCnpj(String cnpj) {
        if (cnpj == null) {
            this.cnpj = null;
        } else {
            // Remove all non-digits: "12.345.678/0001-90" → "12345678000190"
            this.cnpj = cnpj.replaceAll("\\D", "");
        }
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        if (inscricaoEstadual == null) {
            this.inscricaoEstadual = null;
        } else {
            // Remove all non-digits (some IEs have dots, dashes, or letters)
            this.inscricaoEstadual = inscricaoEstadual.replaceAll("\\D", "");
        }
    }

    // Optional: Add validation before persist/update
    @PrePersist
    @PreUpdate
    private void validateFields() {
        // CNPJ must have exactly 14 digits
        if (cnpj != null && cnpj.length() != 14) {
            throw new IllegalStateException("CNPJ must have exactly 14 digits");
        }

        // Inscrição Estadual must have between 9 and 14 digits (varies by state)
        if (inscricaoEstadual != null && (inscricaoEstadual.length() < 8 || inscricaoEstadual.length() > 14)) {
            throw new IllegalStateException("Inscrição Estadual must have between 8 and 14 digits");
        }

        // Company cannot be created in the future
        if (dataCriacaoEmpresa != null && dataCriacaoEmpresa.isAfter(LocalDate.now())) {
            throw new IllegalStateException("Data de criação da empresa não pode ser no futuro");
        }

        // Optional: Company must be at least 1 year old to register (business rule)
        if (dataCriacaoEmpresa != null && dataCriacaoEmpresa.isAfter(LocalDate.now().minusYears(1))) {
            throw new IllegalStateException("Empresa deve ter pelo menos 1 ano de existência");
        }
    }
}