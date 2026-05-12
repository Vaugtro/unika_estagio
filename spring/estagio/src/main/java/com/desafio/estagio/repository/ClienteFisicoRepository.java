package com.desafio.estagio.repository;

import com.desafio.estagio.model.ClienteFisico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteFisicoRepository extends JpaRepository<ClienteFisico, Long> {

    // ========== CPF Operations ==========

    /**
     * Find by exact CPF (cleaned, 11 digits)
     */
    Optional<ClienteFisico> findByCpf(String cpf);

    /**
     * Check if CPF exists
     */
    boolean existsByCpf(String cpf);

    /**
     * Find by CPF with active status
     */
    Optional<ClienteFisico> findByCpfAndEstaAtivoTrue(String cpf);

    // ========== Status Operations ==========

    /**
     * Find all active clients with pagination
     */
    Page<ClienteFisico> findByEstaAtivoTrue(Pageable pageable);

    /**
     * Find all inactive clients with pagination
     */
    Page<ClienteFisico> findByEstaAtivoFalse(Pageable pageable);

    /**
     * Count active clients
     */
    long countByEstaAtivoTrue();

    /**
     * Count inactive clients
     */
    long countByEstaAtivoFalse();

    // ========== Search Operations ==========

    /**
     * Search by CPF with LIKE (for partial matches)
     */
    @Query("SELECT c FROM ClienteFisico c WHERE c.cpf LIKE %:cpf%")
    Page<ClienteFisico> searchByCpf(@Param("cpf") String cpf, Pageable pageable);

    /**
     * Search by name
     */
    Page<ClienteFisico> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    /**
     * Search by email
     */
    Optional<ClienteFisico> findByEmail(String email);

    /**
     * Search by RG
     */
    Optional<ClienteFisico> findByRg(String rg);

    boolean existsByRg(String rg);

    // ========== Date Range Operations ==========

    /**
     * Find clients by birth date range
     */
    Page<ClienteFisico> findByDataNascimentoBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Find clients born after a specific date
     */
    Page<ClienteFisico> findByDataNascimentoAfter(LocalDate date, Pageable pageable);

    // ========== Bulk Operations ==========

    /**
     * Inactivate all clients by list of IDs
     */
    @Modifying
    @Query("UPDATE ClienteFisico c SET c.estaAtivo = false WHERE c.id IN :ids")
    void inactivateAllByIds(@Param("ids") List<Long> ids);

    /**
     * Activate all clients by list of IDs
     */
    @Modifying
    @Query("UPDATE ClienteFisico c SET c.estaAtivo = true WHERE c.id IN :ids")
    void activateAllByIds(@Param("ids") List<Long> ids);
}