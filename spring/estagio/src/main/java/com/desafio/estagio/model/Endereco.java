package com.desafio.estagio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Endereco")
@Builder
@Table(name = "endereco")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk", columnDefinition = "INT UNSIGNED")
    private Long id;

    @Column(name = "logradouro", nullable = false)
    private String logradouro;

    @Column(name = "numero", nullable = false, columnDefinition = "INT UNSIGNED")
    private Long numero;

    @Column(name = "cep", nullable = false, length = 8)
    private String cep;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "telefone", nullable = false, length = 11)
    private String telefone;

    @Column(name = "cidade", nullable = false)
    private String cidade;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Builder.Default
    @Column(name = "endereco_principal", nullable = false)
    private Boolean principal = false;

    @Column(name = "complemento")
    private String complemento;

    @ManyToOne(optional = false, targetEntity = Cliente.class)
    @JoinColumn(name = "cliente_id", nullable = false, updatable = false)
    @JsonIgnore
    private Cliente cliente;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Manual setter for CEP with cleaning
    public void setCep(String cep) {
        if (cep == null) {
            this.cep = null;
        } else {
            this.cep = cep.replaceAll("\\D", "");
        }
    }

    // Manual setter for Telefone with cleaning
    public void setTelefone(String telefone) {
        if (telefone == null) {
            this.telefone = null;
        } else {
            this.telefone = telefone.replaceAll("\\D", "");
        }
    }

    public Boolean isPrincipal() {
        return this.principal;
    }

    @PreRemove
    private void preRemove() {
        if (cliente != null && principal) {
            throw new IllegalStateException("Não é possível remover o endereço principal sem definir outro como principal");
        }
    }
}