package com.desafio.estagio.repository;

import com.desafio.estagio.model.ClienteJuridico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteJuridicoRepository extends JpaRepository<ClienteJuridico, Long> {

    // ========== CNPJ Operations ==========

    /**
     * Find by exact CNPJ (cleaned, 14 digits)
     */
    Optional<ClienteJuridico> findByCnpj(String cnpj);

    /**
     * Check if CNPJ exists
     */
    boolean existsByCnpj(String cnpj);

    /**
     * Find by CNPJ with active status
     */
    Optional<ClienteJuridico> findByCnpjAndEstaAtivoTrue(String cnpj);

    // ========== Status Operations ==========

    /**
     * Find all active clients with pagination
     */
    Page<ClienteJuridico> findByEstaAtivoTrue(Pageable pageable);

    /**
     * Find all inactive clients with pagination
     */
    Page<ClienteJuridico> findByEstaAtivoFalse(Pageable pageable);

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
     * Search by CNPJ with LIKE (for partial matches)
     */
    @Query("SELECT c FROM ClienteJuridico c WHERE c.cnpj LIKE %:cnpj%")
    Page<ClienteJuridico> searchByCnpj(@Param("cnpj") String cnpj, Pageable pageable);

    /**
     * Search by razão social (company name)
     */
    Page<ClienteJuridico> findByRazaoSocialContainingIgnoreCase(String razaoSocial, Pageable pageable);

    /**
     * Search by email
     */
    Optional<ClienteJuridico> findByEmail(String email);

    // ========== Date Range Operations ==========

    /**
     * Find companies created after a specific date
     */
    Page<ClienteJuridico> findByDataCriacaoEmpresaAfter(java.time.LocalDate date, Pageable pageable);

    /**
     * Find companies created between dates
     */
    Page<ClienteJuridico> findByDataCriacaoEmpresaBetween(java.time.LocalDate startDate, java.time.LocalDate endDate, Pageable pageable);

    // ========== Bulk Operations ==========

    /**
     * Inactivate all clients by list of IDs
     */
    @Modifying
    @Query("UPDATE ClienteJuridico c SET c.estaAtivo = false WHERE c.id IN :ids")
    void inactivateAllByIds(@Param("ids") List<Long> ids);

    /**
     * Activate all clients by list of IDs
     */
    @Modifying
    @Query("UPDATE ClienteJuridico c SET c.estaAtivo = true WHERE c.id IN :ids")
    void activateAllByIds(@Param("ids") List<Long> ids);
}