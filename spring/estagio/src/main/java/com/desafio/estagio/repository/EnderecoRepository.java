package com.desafio.estagio.repository;


import com.desafio.estagio.model.Endereco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    Page<Endereco> findByClienteId(Long clienteId, Pageable pageable);

    List<Endereco> findByClienteId(Long clienteId);

    Optional<Endereco> findByClienteIdAndPrincipalTrue(Long clienteId);

    long countByClienteId(Long clienteId);

    boolean existsByClienteIdAndPrincipalTrue(Long clienteId);

    long deleteByClienteId(Long clienteId);

    /**
     * Fuzzy search across all enderecos (logradouro, bairro, cidade, cep)
     */
    @Query("SELECT e FROM Endereco e WHERE "
            + "LOWER(e.logradouro) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "LOWER(e.bairro)     LIKE LOWER(CONCAT('%', :q, '%')) OR "
            +             "LOWER(e.municipio.nome) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "LOWER(e.cep)        LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Endereco> search(@Param("q") String q, Pageable pageable);

    /**
     * Count results for global endereco fuzzy search
     */
    @Query("SELECT COUNT(e) FROM Endereco e WHERE "
            + "LOWER(e.logradouro) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "LOWER(e.bairro)     LIKE LOWER(CONCAT('%', :q, '%')) OR "
            +             "LOWER(e.municipio.nome) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "LOWER(e.cep)        LIKE LOWER(CONCAT('%', :q, '%'))")
    long countSearch(@Param("q") String q);

    /**
     * Fuzzy search across enderecos scoped to a specific cliente
     */
    @Query("SELECT e FROM Endereco e WHERE e.cliente.id = :clienteId AND ("
            + "LOWER(e.logradouro) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "LOWER(e.bairro)     LIKE LOWER(CONCAT('%', :q, '%')) OR "
            +             "LOWER(e.municipio.nome) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "LOWER(e.cep)        LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Endereco> searchByClienteId(@Param("clienteId") Long clienteId, @Param("q") String q, Pageable pageable);

    /**
     * Count results for scoped endereco fuzzy search
     */
    @Query("SELECT COUNT(e) FROM Endereco e WHERE e.cliente.id = :clienteId AND ("
            + "LOWER(e.logradouro) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "LOWER(e.bairro)     LIKE LOWER(CONCAT('%', :q, '%')) OR "
            +             "LOWER(e.municipio.nome) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "LOWER(e.cep)        LIKE LOWER(CONCAT('%', :q, '%')))")
    long countSearchByClienteId(@Param("clienteId") Long clienteId, @Param("q") String q);
}