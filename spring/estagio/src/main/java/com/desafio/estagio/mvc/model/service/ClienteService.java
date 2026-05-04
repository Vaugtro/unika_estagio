package com.desafio.estagio.mvc.model.service;

import com.desafio.estagio.mvc.model.dto.ClienteDTO;
import com.desafio.estagio.mvc.model.entity.ClienteEntity;
import com.desafio.estagio.repository.ClienteRepository;
import java.util.List;

// Use R extends ClienteRepository<T> to keep it dynamic
public interface ClienteService<T extends ClienteEntity, S extends ClienteDTO.Response, R extends ClienteRepository<T>> {
    void inativarCliente(Long id);
    void ativarCliente(Long id);
    boolean existsById(Long id);
    T findEntityById(Long id);
    List<S> findAll();
}