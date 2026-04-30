package com.desafio.estagio.mvc.model.service;

import com.desafio.estagio.mvc.model.entity.ICliente;
import com.desafio.estagio.repository.ClienteRepository;

public interface ClienteService<T extends ICliente, R extends ClienteRepository<T>> {
    void inativarCliente(Long id);

    void ativarCliente(Long id);
}
