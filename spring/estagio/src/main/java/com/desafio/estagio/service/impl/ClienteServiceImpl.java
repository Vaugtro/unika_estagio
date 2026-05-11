package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.ClienteDTO;
import com.desafio.estagio.model.ClienteEntity;
import com.desafio.estagio.repository.ClienteRepository;
import com.desafio.estagio.service.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Abstract service implementation for Cliente entities with generic type support
 *
 * @param <T> The entity type (extends ClienteEntity)
 * @param <S> The DTO response type (extends ClienteDTO.Response)
 * @param <R> The repository type (extends ClienteRepository)
 */
public abstract class ClienteServiceImpl<T extends ClienteEntity, S extends ClienteDTO.Response, R extends ClienteRepository<T>>
        implements ClienteService<T, S> {

    private static final Logger logger = LoggerFactory.getLogger(ClienteServiceImpl.class);
    private static final String REGISTRO_NAO_ENCONTRADO = "Registro não encontrado com o ID: %d";
    private static final String CLIENTE_SALVO = "Cliente {} salvo com sucesso";
    private static final String CLIENTE_ATUALIZADO = "Cliente {} atualizado com sucesso";
    private static final String CLIENTE_DELETADO = "Cliente {} deletado com sucesso";
    private static final String CLIENTE_ATIVADO = "Cliente {} ativado com sucesso";
    private static final String CLIENTE_INATIVADO = "Cliente {} inativado com sucesso";

    protected final R repository;

    /**
     * Constructor
     *
     * @param repository the repository instance
     */
    protected ClienteServiceImpl(R repository) {
        this.repository = repository;
    }

    /**
     * Maps entity to DTO (must be implemented by subclasses)
     *
     * @param entity the entity to map
     * @return the DTO
     */
    protected abstract S toDTO(T entity);

    /**
     * Maps DTO to entity (must be implemented by subclasses)
     *
     * @param dto the DTO to map
     * @return the entity
     */
    protected abstract T toEntity(S dto);

    /**
     * Maps DTO to entity for updates, preserving the ID
     *
     * @param id the entity ID
     * @param dto the DTO with updated data
     * @param existing the existing entity
     * @return the updated entity
     */
    protected abstract T toEntity(Long id, S dto, T existing);

    // ========== CRUD Operations ==========

    @Override
    public T findById(Long id) throws EntityNotFoundException {
        logger.debug("Buscando cliente com ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    var mensagem = String.format(REGISTRO_NAO_ENCONTRADO, id);
                    logger.warn("Cliente não encontrado: {}", id);
                    return new EntityNotFoundException(mensagem);
                });
    }

    @Override
    public S findByIdDTO(Long id) throws EntityNotFoundException {
        logger.debug("Buscando cliente DTO com ID: {}", id);
        return toDTO(findById(id));
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        logger.debug("Buscando todos os clientes com paginação: página={}, tamanho={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return repository.findAll(pageable);
    }

    @Override
    public Page<S> findAllDTO(Pageable pageable) {
        logger.debug("Buscando todos os clientes DTO com paginação");
        return findAll(pageable).map(this::toDTO);
    }

    @Override
    public T save(T entity) {
        logger.info("Salvando cliente: {}", entity.getId() != null ? entity.getId() : "novo");
        T saved = repository.save(entity);
        logger.info(CLIENTE_SALVO, saved.getId());
        return saved;
    }

    @Override
    public S saveDTO(S dto) {
        logger.info("Salvando cliente a partir de DTO");
        T entity = toEntity(dto);
        T saved = save(entity);
        return toDTO(saved);
    }

    @Override
    public T update(Long id, T entity) throws EntityNotFoundException {
        logger.info("Atualizando cliente com ID: {}", id);
        T existing = findById(id);

        // Preserve ID
        entity.setId(id);

        T updated = repository.save(entity);
        logger.info(CLIENTE_ATUALIZADO, id);
        return updated;
    }

    @Override
    public S updateDTO(Long id, S dto) throws EntityNotFoundException {
        logger.info("Atualizando cliente DTO com ID: {}", id);
        T existing = findById(id);
        T entity = toEntity(id, dto, existing);

        T updated = repository.save(entity);
        logger.info(CLIENTE_ATUALIZADO, id);
        return toDTO(updated);
    }

    @Override
    public void deleteById(Long id) throws EntityNotFoundException {
        logger.info("Deletando cliente com ID: {}", id);
        if (!existsById(id)) {
            var mensagem = String.format(REGISTRO_NAO_ENCONTRADO, id);
            logger.warn("Tentativa de deletar cliente inexistente: {}", id);
            throw new EntityNotFoundException(mensagem);
        }
        repository.deleteById(id);
        logger.info(CLIENTE_DELETADO, id);
    }

    // ========== Status Operations ==========

    @Override
    public void inativarCliente(Long id) throws EntityNotFoundException {
        logger.info("Inativando cliente com ID: {}", id);
        T cliente = findById(id);
        cliente.setEstaAtivo(false);
        repository.save(cliente);
        logger.info(CLIENTE_INATIVADO, id);
    }

    @Override
    public void ativarCliente(Long id) throws EntityNotFoundException {
        logger.info("Ativando cliente com ID: {}", id);
        T cliente = findById(id);
        cliente.setEstaAtivo(true);
        repository.save(cliente);
        logger.info(CLIENTE_ATIVADO, id);
    }

    // ========== Query Operations ==========

    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            logger.debug("Verificação de existência com ID nulo retorna false");
            return false;
        }
        boolean exists = repository.existsById(id);
        logger.debug("Cliente com ID {} existe: {}", id, exists);
        return exists;
    }

    @Override
    public long count() {
        long total = repository.count();
        logger.debug("Total de clientes: {}", total);
        return total;
    }

    @Override
    public long countAtivos() {
        long total = repository.countByEstaAtivoTrue();
        logger.debug("Total de clientes ativos: {}", total);
        return total;
    }

    @Override
    public long countInativos() {
        long total = repository.countByEstaAtivoFalse();
        logger.debug("Total de clientes inativos: {}", total);
        return total;
    }
}