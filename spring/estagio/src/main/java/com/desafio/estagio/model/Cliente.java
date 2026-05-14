package com.desafio.estagio.model;

import com.desafio.estagio.model.enums.TipoCliente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Cliente")
@SuperBuilder
@Table(name = "cliente")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk", columnDefinition = "INT UNSIGNED")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 8)
    private TipoCliente tipo;

    @Column(name = "email", nullable = false)
    @Email()
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "ativo", nullable = false)
    private Boolean estaAtivo = true;

    @Builder.Default
    @OneToMany(targetEntity = Endereco.class, mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Endereco> enderecos = new ArrayList<>();

    @PostLoad
    @PostPersist
    @PostUpdate
    private void validateEnderecos() {
        // Garante que a lista não seja null
        if (enderecos == null) {
            enderecos = new ArrayList<>();
        }
    }

    /**
     * Adiciona um endereço garantindo as regras de negócio
     */
    public void addEndereco(Endereco endereco) {
        if (endereco == null) {
            throw new IllegalArgumentException("Endereço não pode ser nulo");
        }

        if (enderecos == null) {
            enderecos = new ArrayList<>();
        }

        // Se for o primeiro endereço, ele deve ser principal
        if (enderecos.isEmpty()) {
            endereco.setPrincipal(true);
        }

        // Se o endereço a ser adicionado for principal, remove o status dos outros
        if (endereco.isPrincipal()) {
            enderecos.forEach(e -> e.setPrincipal(false));
        }

        enderecos.add(endereco);
        endereco.setCliente(this);
    }

    /**
     * Remove um endereço garantindo que pelo menos um endereço permaneça
     */
    public void removeEndereco(Endereco endereco) {
        if (enderecos == null || enderecos.isEmpty()) {
            throw new IllegalStateException("Cliente não possui endereços para remover");
        }

        if (enderecos.size() <= 1) {
            throw new IllegalStateException("Cliente deve ter pelo menos um endereço. Não é possível remover o único endereço.");
        }

        boolean wasPrincipal = endereco.isPrincipal();
        boolean removed = enderecos.remove(endereco);

        if (removed) {
            endereco.setCliente(null);

            // Se o endereço removido era o principal, define o primeiro como principal
            if (wasPrincipal && !enderecos.isEmpty()) {
                enderecos.get(0).setPrincipal(true);
            }
        }
    }

    /**
     * Obtém o endereço principal
     */
    public Endereco getEnderecoPrincipal() {
        if (enderecos == null || enderecos.isEmpty()) {
            return null;
        }

        return enderecos.stream()
                .filter(Endereco::isPrincipal)
                .findFirst()
                .orElse(enderecos.get(0));
    }

    /**
     * Define um endereço como principal
     */
    public void setEnderecoPrincipal(Endereco endereco) {
        if (endereco == null) {
            throw new IllegalArgumentException("Endereço não pode ser nulo");
        }

        if (!enderecos.contains(endereco)) {
            throw new IllegalArgumentException("Endereço não pertence a este cliente");
        }

        // Remove status principal de todos
        enderecos.forEach(e -> e.setPrincipal(false));

        // Define o novo endereço como principal
        endereco.setPrincipal(true);
    }

    /**
     * Atualiza a lista de endereços garantindo as regras de negócio
     */
    public void setEnderecos(List<Endereco> enderecos) {
        if (enderecos == null || enderecos.isEmpty()) {
            throw new IllegalArgumentException("Cliente deve ter pelo menos um endereço");
        }

        // Garante que pelo menos um endereço seja principal
        boolean hasPrincipal = enderecos.stream().anyMatch(Endereco::isPrincipal);

        if (!hasPrincipal) {
            enderecos.get(0).setPrincipal(true);
        }

        // Limpa a relação anterior
        if (this.enderecos != null) {
            this.enderecos.forEach(e -> e.setCliente(null));
        }

        this.enderecos = new ArrayList<>(enderecos);

        // Estabelece a relação bidirecional
        this.enderecos.forEach(e -> e.setCliente(this));
    }

    /**
     * Verifica se o cliente tem um endereço principal
     */
    public boolean hasEnderecoPrincipal() {
        return getEnderecoPrincipal() != null;
    }

    /**
     * Copies properties from another cliente
     */
    public abstract void copyFrom(Cliente source);
}