package com.desafio.estagio.mvc.model.entity;

import com.desafio.estagio.mvc.model.dto.TipoCliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cliente")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ClienteEntity implements Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk", columnDefinition = "INT UNSIGNED")
    @Getter
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 8)
    @Getter
    @Setter
    private TipoCliente tipo;

    @Column(name = "email", nullable = false)
    @Email()
    @Getter
    @Setter
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    @Getter
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    @Getter
    private LocalDateTime updatedAt;

    @Column(name = "ativo", nullable = false)
    private Boolean estaAtivo = true;

    @OneToMany(targetEntity = EnderecoEntity.class, mappedBy = "cliente")
    private Set<Endereco> enderecos;


    // --- Methods
    public Boolean estaAtivo() {
        return estaAtivo;
    }

    public void estaAtivoActivate() {
        this.estaAtivo = true;
    }

    public void estaAtivoDeactivate() {
        this.estaAtivo = false;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
