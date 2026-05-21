package com.desafio.estagio.service;

import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.exceptions.ResourceNotFoundException;
import com.desafio.estagio.model.Cliente;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public abstract class AbstractClienteService<T extends Cliente, R extends JpaRepository<T, Long>> {

    protected final R repository;

    public T findModelById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getEntityName() + " não encontrado com o ID: " + id));
    }

    protected void ensureIsActive(T model) {
        if (Boolean.FALSE.equals(model.getEstaAtivo())) {
            throw new BusinessException("Operação não permitida: O cliente está inativo.");
        }
    }

    @Transactional
    public void delete(Long id) {
        inactivate(id);
    }

    @Transactional
    public void activate(Long id) {
        log.debug("Activating {} with ID: {}", getEntityName(), id);
        T model = findModelById(id);

        if (Boolean.TRUE.equals(model.getEstaAtivo())) {
            throw new BusinessException("Este cliente já está ativo.");
        }

        model.setEstaAtivo(true);
        repository.save(model);
        log.info("Activated {} with ID: {}", getEntityName(), id);
    }

    @Transactional
    public void inactivate(Long id) {
        log.debug("Inactivating {} with ID: {}", getEntityName(), id);
        T model = findModelById(id);

        if (Boolean.FALSE.equals(model.getEstaAtivo())) {
            throw new BusinessException("Este cliente já está inativo.");
        }

        model.setEstaAtivo(false);
        repository.save(model);
        log.info("Inactivated {} with ID: {}", getEntityName(), id);
    }

    @Transactional
    public void hardDelete(Long id) {
        log.debug("Hard deleting {} with ID: {}", getEntityName(), id);
        T model = findModelById(id);
        repository.delete(model);
        log.info("Hard deleted {} with ID: {}", getEntityName(), id);
    }

    public long count() {
        return repository.count();
    }


    protected abstract String getEntityName();
}
