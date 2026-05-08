package com.desafio.estagio.service;

import com.desafio.estagio.dto.ClienteDTO;
import com.desafio.estagio.model.ClienteEntity;
import com.desafio.estagio.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClienteServiceImpl<T extends ClienteEntity, S extends ClienteDTO.Response, R extends ClienteRepository<T>>
        implements ClienteService<T, S, R> {

    private static final Logger logger = LoggerFactory.getLogger(ClienteServiceImpl.class);
    private static final String REGISTRO_NAO_ENCONTRADO = "Registro não encontrado com o ID: %d";

    protected final R repository;

    protected ClienteServiceImpl(R repository) {
        this.repository = repository;
    }

    /**
     * Finds an entity by ID without mapping to DTO
     *
     * @param id the entity ID
     * @return the found entity
     * @throws EntityNotFoundException if entity not found
     */
    public T findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(REGISTRO_NAO_ENCONTRADO, id)));
    }

    /**
     * Checks if an entity exists by ID
     *
     * @param id the entity ID
     * @return true if exists, false otherwise
     */
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        return repository.existsById(id);
    }

    /**
     * Finds an entity by ID (alias for findEntityById)
     *
     * @param id the entity ID
     * @return the found entity
     * @throws EntityNotFoundException if entity not found
     */
    public T findById(Long id) {
        return findEntityById(id);
    }

    /**
     * Deactivates a client
     *
     * @param id the client ID
     * @throws EntityNotFoundException if client not found
     */
    public void inativarCliente(Long id) {
        try {
            T cliente = findEntityById(id);
            cliente.setEstaAtivo(false);
            repository.save(cliente);
            logger.info("Cliente {} inativado com sucesso", id);
        } catch (EntityNotFoundException e) {
            logger.error("Falha ao inativar cliente {}: registro não encontrado", id);
            throw e;
        }
    }

    /**
     * Activates a client
     *
     * @param id the client ID
     * @throws EntityNotFoundException if client not found
     */
    public void ativarCliente(Long id) {
        try {
            T cliente = findEntityById(id);
            cliente.setEstaAtivo(true);
            repository.save(cliente);
            logger.info("Cliente {} ativado com sucesso", id);
        } catch (EntityNotFoundException e) {
            logger.error("Falha ao ativar cliente {}: registro não encontrado", id);
            throw e;
        }
    }

    /**
     * Saves an entity to the repository
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    protected T save(T entity) {
        T saved = repository.save(entity);
        logger.debug("Entidade {} salva com sucesso", saved.getId());
        return saved;
    }

    /**
     * Deletes an entity by ID
     *
     * @param id the entity ID
     * @throws EntityNotFoundException if entity not found
     */
    public void deleteById(Long id) {
        if (!existsById(id)) {
            throw new EntityNotFoundException(String.format(REGISTRO_NAO_ENCONTRADO, id));
        }
        repository.deleteById(id);
        logger.info("Cliente {} deletado com sucesso", id);
    }

    @Override
    public long count() {
        return repository.count();
    }
}