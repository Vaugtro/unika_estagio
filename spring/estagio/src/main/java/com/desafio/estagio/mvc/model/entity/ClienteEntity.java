package com.desafio.estagio.mvc.model.entity;

import com.desafio.estagio.mvc.model.dto.TipoCliente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @Setter
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
    @Getter @Setter
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    @Getter @Setter
    private LocalDateTime updatedAt;

    @Column(name = "ativo", nullable = false)
    @Getter @Setter
    private Boolean estaAtivo = true;

    @OneToMany(targetEntity = EnderecoEntity.class, mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    @JsonIgnore
    private List<Endereco> enderecos;

    public void addEndereco(EnderecoEntity endereco) {
        if (enderecos == null) {
            enderecos = new ArrayList<>();
        }
        enderecos.add(endereco);
        endereco.setCliente(this);
    }

    public void removeEndereco(EnderecoEntity endereco) {
        if (enderecos != null) {
            enderecos.remove(endereco);
            endereco.setCliente(null);
        }
    }
}
