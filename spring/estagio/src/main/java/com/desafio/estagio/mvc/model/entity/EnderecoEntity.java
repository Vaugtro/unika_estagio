package com.desafio.estagio.mvc.model.entity;

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
    @Setter
    private String cep;

    @Column(name = "bairro")
    @Getter
    @Setter
    private String bairro;

    @Column(name = "telefone", nullable = false, length = 11)
    @Getter
    @Setter
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
    @Getter @Setter
    private Boolean eEnderecoPrincipal = false;

    @Column(name = "complemento")
    @Getter @Setter
    private String complemento;

    @ManyToOne(optional = false, targetEntity = ClienteEntity.class)
    @JoinColumn(name = "cliente_id", nullable = false, updatable = false)
    @Getter
    private Cliente cliente;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    @Getter
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    @Getter
    private LocalDateTime updatedAt;

    public Boolean eEnderecoPrincipal() {
        return eEnderecoPrincipal;
    }

    public void eEnderecoPrincipalActivate() {
        this.eEnderecoPrincipal = true;
    }

    public void eEnderecoPrincipalDeactivate() {
        this.eEnderecoPrincipal = false;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
