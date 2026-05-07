package com.desafio.estagio.mvc.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "endereco")
public class EnderecoEntity implements Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk", columnDefinition = "INT UNSIGNED")
    @Getter
    @Setter
    private Long id;

    @Column(name = "logradouro", nullable = false)
    @Getter
    @Setter
    private String logradouro;

    @Column(name = "numero", nullable = false, columnDefinition = "INT UNSIGNED")
    @Getter
    @Setter
    private Long numero;

    @Column(name = "cep", nullable = false, length = 8)
    @Getter
    private String cep;

    @Column(name = "bairro")
    @Getter
    @Setter
    private String bairro;

    @Column(name = "telefone", nullable = false, length = 11)
    @Getter
    private String telefone;

    @Column(name = "cidade", nullable = false)
    @Getter
    @Setter
    private String cidade;

    @Column(name = "estado", nullable = false)
    @Getter
    @Setter
    private String estado;

    @Column(name = "endereco_principal", nullable = false)
    @Getter
    @Setter
    private Boolean principal = false;

    @Column(name = "complemento")
    @Getter
    @Setter
    private String complemento;

    @ManyToOne(optional = false, targetEntity = ClienteEntity.class)
    @JoinColumn(name = "cliente_id", nullable = false, updatable = false)
    @Setter
    @Getter
    @JsonIgnore
    private Cliente cliente;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    @Getter
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    @Getter
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

    @Override
    public Boolean isPrincipal() {
        return this.principal;
    }
}