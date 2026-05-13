package com.desafio.estagio.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ClienteFisico")
@SuperBuilder
@Table(name = "cliente_fisico")
public class ClienteFisico extends Cliente {

    // Manual getter and setter for CPF with cleaning
    @Column(name = "cpf", unique = true, nullable = false, length = 11)
    private String cpf;  // Removed Lombok annotation - we control the setter

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "rg", nullable = false, length = 9)
    private String rg;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    public void setCpf(String cpf) {
        // Clean the CPF before storing
        if (cpf == null) {
            this.cpf = null;
        } else {
            this.cpf = cpf.replaceAll("\\D", "");
        }
    }

    @Override
    public void copyFrom(Cliente source) {
        if (!(source instanceof ClienteFisico s)) {
            throw new IllegalArgumentException("Source must be an instance of ClienteFisico");
        }
        this.setNome(s.getNome());
        this.setCpf(s.getCpf());
        this.setRg(s.getRg());
        this.setDataNascimento(s.getDataNascimento());
    }
}