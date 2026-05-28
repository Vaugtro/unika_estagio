package com.desafio.estagio.service.lifecycle;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoCreateRequest;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoResponse;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoUpdateRequest;
import jakarta.validation.Valid;

/**
 * Write and lifecycle operations for ClienteFisico.
 */
public interface ClienteFisicoLifecycleService {

    ClienteFisicoResponse create(@Valid ClienteFisicoCreateRequest createRequest);

    ClienteFisicoResponse update(Long id, @Valid ClienteFisicoUpdateRequest updateRequest);

    void delete(Long id);

    void hardDelete(Long id);

    void activate(Long id);

    void inactivate(Long id);
}
