package com.desafio.estagio.service.lifecycle;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoCreateRequest;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoResponse;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoUpdateRequest;

/**
 * Write and lifecycle operations for ClienteJuridico.
 */
public interface ClienteJuridicoLifecycleService {

    ClienteJuridicoResponse create(ClienteJuridicoCreateRequest request);

    ClienteJuridicoResponse update(Long id, ClienteJuridicoUpdateRequest request);

    void delete(Long id);

    void hardDelete(Long id);

    void activate(Long id);

    void inactivate(Long id);
}
