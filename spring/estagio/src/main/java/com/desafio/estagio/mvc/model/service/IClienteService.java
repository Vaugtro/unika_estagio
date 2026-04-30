package com.desafio.estagio.mvc.model.service;

import com.desafio.estagio.mvc.model.entity.ICliente;
import com.desafio.estagio.repository.ClienteRepository;

public abstract class IClienteService<T extends ICliente, R extends ClienteRepository<T>> implements ClienteService<T, R> {
    protected final R repository;

    public IClienteService(R repository) {
        this.repository = repository;
    }

    public void inativarCliente(Long id) {
        repository.findById(id).ifPresent(cliente -> {
            cliente.estaAtivoDeactivate();
            repository.save(cliente);
        });
    }

    public void ativarCliente(Long id) {
        repository.findById(id).ifPresent(cliente -> {
            cliente.estaAtivoActivate();
            repository.save(cliente);
        });
    }
}
