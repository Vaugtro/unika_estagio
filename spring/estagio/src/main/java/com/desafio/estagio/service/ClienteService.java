package com.desafio.estagio.service;

import com.desafio.estagio.dto.ClienteDTO;
import com.desafio.estagio.model.ClienteEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Generic service interface for Cliente entities
 *
 * @param <T> The entity type
 * @param <S> The DTO response type
 */
public interface ClienteService<T extends ClienteEntity, S extends ClienteDTO.Response> {

    // ========== CRUD Operations ==========

    /**
     * Finds an entity by ID
     *
     * @param id the entity ID
     * @return the entity
     * @throws EntityNotFoundException if not found
     */
    T findById(Long id) throws EntityNotFoundException;

    /**
     * Finds an entity by ID and maps to DTO
     *
     * @param id the entity ID
     * @return the DTO
     * @throws EntityNotFoundException if not found
     */
    S findByIdDTO(Long id) throws EntityNotFoundException;

    /**
     * Finds all entities with pagination
     *
     * @param pageable pagination info
     * @return page of entities
     */
    Page<T> findAll(Pageable pageable);

    /**
     * Finds all entities as DTOs with pagination
     *
     * @param pageable pagination info
     * @return page of DTOs
     */
    Page<S> findAllDTO(Pageable pageable);

    /**
     * Saves an entity
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    T save(T entity);

    /**
     * Saves from DTO
     *
     * @param dto the DTO to save
     * @return the saved DTO
     */
    S saveDTO(S dto);

    /**
     * Deletes an entity by ID
     *
     * @param id the entity ID
     * @throws EntityNotFoundException if not found
     */
    void deleteById(Long id) throws EntityNotFoundException;

    // ========== Status Operations ==========

    /**
     * Deactivates a client
     *
     * @param id the client ID
     * @throws EntityNotFoundException if not found
     */
    void inativarCliente(Long id) throws EntityNotFoundException;

    /**
     * Activates a client
     *
     * @param id the client ID
     * @throws EntityNotFoundException if not found
     */
    void ativarCliente(Long id) throws EntityNotFoundException;

    // ========== Query Operations ==========

    /**
     * Checks if an entity exists
     *
     * @param id the entity ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Gets total count of entities
     *
     * @return total count
     */
    long count();

    /**
     * Gets count of active entities
     *
     * @return count of active entities
     */
    long countAtivos();

    /**
     * Gets count of inactive entities
     *
     * @return count of inactive entities
     */
    long countInativos();
}