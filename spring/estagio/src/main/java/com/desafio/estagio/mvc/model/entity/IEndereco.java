package com.desafio.estagio.mvc.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyDiscriminatorValue;
import org.hibernate.annotations.AnyKeyJavaClass;

@Entity
public class IEndereco implements Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="pk", columnDefinition = "INT UNSIGNED")
    @Getter
    private Long id;

    @Column(name = "logradouro", nullable = false)
    @Getter @Setter
    private String logradouro;

    @Column(name="numero", nullable = false, columnDefinition = "INT UNSIGNED")
    @Getter @Setter
    private long numero;

    @Column(name="cep", nullable = false, length = 8)
    @Getter @Setter
    private String cep;

    @Column(name="bairro")
    @Getter @Setter
    private String bairro;

    @Column(name="cidade", nullable = false)
    @Getter @Setter
    private String cidade;

    @Column(name="estado", nullable = false)
    @Getter @Setter
    private String estado;

    @Column(name = "endereco_principal", nullable = false)
    private Boolean endereco_principal = false;

    @Column(name = "complemnto")
    private String complemento;
}
