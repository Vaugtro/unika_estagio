package com.desafio.estagio.mvc.model.entity;

import com.desafio.estagio.mvc.model.dto.TipoCliente;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;

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
    private Date dataNascimento;

    // --- Constructor

    public IClienteFisico(Long id, TipoCliente tipo, String email, LocalDateTime createdAt, LocalDateTime updatedAt, Boolean status) {
        super(id, tipo, email, createdAt, updatedAt, status);
    }
}
