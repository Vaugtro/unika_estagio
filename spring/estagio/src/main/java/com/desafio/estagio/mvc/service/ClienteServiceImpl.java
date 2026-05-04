package com.desafio.estagio.mvc.service;

import com.desafio.estagio.mvc.model.dto.ClienteDTO;
import com.desafio.estagio.mvc.model.entity.ClienteEntity;
import com.desafio.estagio.repository.ClienteRepository;

public abstract class ClienteServiceImpl<T extends ClienteEntity, S extends ClienteDTO.Response, R extends ClienteRepository<T>>
        implements ClienteService<T, S, R> {

    protected final R repository;

    protected ClienteServiceImpl(R repository) {
        this.repository = repository;
    }

    public T findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado com o ID: " + id));
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public void inativarCliente(Long id) {
        T cliente = findEntityById(id);
        cliente.setEstaAtivo(false);
        repository.save(cliente);
    }

    public void ativarCliente(Long id) {
        T cliente = findEntityById(id);
        cliente.setEstaAtivo(true);
        repository.save(cliente);
    }
}