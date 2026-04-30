package com.desafio.estagio.mvc.model.entity;

import com.desafio.estagio.mvc.model.dto.TipoCliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "cliente_fisico")
public class IClienteFisico extends ICliente implements ClienteFisico {
    @Column(name = "cpf", unique = true, nullable = false, length = 11)
    @Getter @Setter
    private String cpf;

    @Column(name = "nome", nullable = false)
    @Getter @Setter
    private String nome;

    @Column( name = "rg", nullable = false, length = 9)
    @Getter @Setter
    private String rg;

    @Column(name = "data_nascimento", nullable = false)
    @Getter @Setter
    private LocalDate dataNascimento;

    // --- Constructor

    public IClienteFisico(Long id, TipoCliente tipo, String email, LocalDateTime createdAt, LocalDateTime updatedAt, Boolean estaAtivo, String cpf, String nome, String rg, LocalDate dataNascimento) {
        super(id, tipo, email, createdAt, updatedAt, estaAtivo);
        this.cpf = cpf;
        this.nome = nome;
        this.rg = rg;
        this.dataNascimento = dataNascimento;
    }

    public IClienteFisico() {

    }
}
