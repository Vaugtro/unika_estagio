package com.desafio.estagio.service;

import com.desafio.estagio.dto.ClienteFisicoDTO;
import com.desafio.estagio.model.ClienteFisico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for ClienteFisico (Individual Client).
 * Defines business operations using modern Spring best practices:
 * - Pagination for all list operations.
 * - RuntimeExceptions (no explicit throws clauses).
 */
public interface ClienteFisicoService {

    /**
     * Create a new ClienteFisico.
     */
    ClienteFisicoDTO.Response create(ClienteFisicoDTO.CreateRequest createRequest);

    /**
     * Update an existing ClienteFisico.
     */
    ClienteFisicoDTO.Response update(Long id, ClienteFisicoDTO.UpdateRequest updateRequest);

    /**
     * Soft delete a ClienteFisico (sets estaAtivo = false).
     */
    void delete(Long id);

    /**
     * Hard delete a ClienteFisico (permanently remove from database).
     */
    void hardDelete(Long id);

    /**
     * Find ClienteFisico by ID.
     */
    ClienteFisicoDTO.Response findById(Long id);

    /**
     * Find all active ClienteFisico with pagination.
     */
    Page<ClienteFisicoDTO.ListResponse> findAllActive(Pageable pageable);

    /**
     * Find all ClienteFisico (including inactive) with pagination.
     */
    Page<ClienteFisicoDTO.ListResponse> findAll(Pageable pageable);

    /**
     * Find all ClienteFisico for report generation with pagination.
     */
    Page<ClienteFisicoDTO.ReportResponse> findAllForReport(Pageable pageable);

    /**
     * Check if a client exists by CPF.
     */
    boolean existsByCpf(String cpf);

    /**
     * Find ClienteFisico by CPF.
     */
    ClienteFisicoDTO.Response findByCpf(String cpf);

    /**
     * Explicitly activate a client.
     */
    void activate(Long id);

    /**
     * Explicitly inactivate a client (alias for delete).
     */
    void inactivate(Long id);

    long count();

    ClienteFisico findEntityById(Long id);
}