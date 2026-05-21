package com.desafio.estagio.wicket.provider;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoListResponse;
import com.desafio.estagio.service.ClienteJuridicoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serial;

public class ClienteJuridicoDataProvider extends AbstractClienteDataProvider<ClienteJuridicoListResponse> {
    @Serial
    private static final long serialVersionUID = 1L;

    private final ClienteJuridicoService service;

    public ClienteJuridicoDataProvider(ClienteJuridicoService service) {
        this.service = service;
    }

    @Override
    protected Page<ClienteJuridicoListResponse> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @Override
    protected long count() {
        return service.count();
    }

    @Override
    protected ClienteJuridicoListResponse findByIdList(Long id) {
        return service.findByIdList(id);
    }

    @Override
    protected Long extractId(ClienteJuridicoListResponse object) {
        return object.id();
    }
}
