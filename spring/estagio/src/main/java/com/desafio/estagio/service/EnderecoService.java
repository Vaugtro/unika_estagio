package com.desafio.estagio.service;

import com.desafio.estagio.dto.endereco.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EnderecoService {

    // =====================================================
    // CREATE OPERATIONS
    // =====================================================

    /**
     * Creates a new address
     * @param request the address data
     * @return the created address
     */
    EnderecoResponse create(EnderecoCreateRequest request);

    /**
     * Creates a new address for a specific client
     * @param clienteId the client ID
     * @param request the address data
     * @return the created address
     */
    EnderecoResponse createForCliente(Long clienteId, EnderecoWithinClienteCreateRequest request);

    // =====================================================
    // READ OPERATIONS
    // =====================================================

    /**
     * Finds an address by its ID
     * @param id the address ID
     * @return the address
     * @throws com.desafio.estagio.exceptions.ResourceNotFoundException if address not found
     */
    EnderecoResponse findById(Long id);

    /**
     * Finds all addresses for a client (with pagination)
     * @param clienteId the client ID
     * @param pageable pagination parameters
     * @return a page of addresses
     */
    Page<EnderecoListResponse> findAllByClienteId(Long clienteId, Pageable pageable);

    /**
     * Finds all addresses for a client (without pagination - use with caution)
     * @param clienteId the client ID
     * @return list of all addresses for the client
     */
    List<EnderecoResponse> findAllByClienteId(Long clienteId);

    /**
     * Finds the principal address for a client
     * @param clienteId the client ID
     * @return the principal address
     * @throws com.desafio.estagio.exceptions.ResourceNotFoundException if no principal address found
     */
    EnderecoResponse findPrincipalByClienteId(Long clienteId);

    /**
     * Counts how many addresses a client has
     * @param clienteId the client ID
     * @return the number of addresses
     */
    long countByClienteId(Long clienteId);

    // =====================================================
    // UPDATE OPERATIONS
    // =====================================================

    /**
     * Updates an existing address
     * @param id the address ID
     * @param request the updated address data
     * @return the updated address
     */
    EnderecoResponse update(Long id, EnderecoUpdateRequest request);

    /**
     * Sets an address as the principal for its client
     * @param id the address ID
     * @return the updated address
     */
    EnderecoResponse setAsPrincipal(Long id);

    // =====================================================
    // DELETE OPERATIONS
    // =====================================================

    /**
     * Deletes an address (soft delete)
     * @param id the address ID
     * @throws com.desafio.estagio.exceptions.BusinessException if it's the last address of the client
     */
    void delete(Long id);

    /**
     * Deletes all addresses for a client
     * @param clienteId the client ID
     */
    void deleteAllByClienteId(Long clienteId);

    // =====================================================
    // VALIDATION OPERATIONS
    // =====================================================

    /**
     * Checks if a client has at least one address
     * @param clienteId the client ID
     * @return true if client has at least one address
     */
    boolean hasAtLeastOneAddress(Long clienteId);

    /**
     * Checks if a client has a principal address
     * @param clienteId the client ID
     * @return true if client has a principal address
     */
    boolean hasPrincipalAddress(Long clienteId);
}