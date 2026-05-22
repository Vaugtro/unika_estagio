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

    private String searchQuery;

    public ClienteJuridicoDataProvider(ClienteJuridicoService service) {
        this.service = service;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    @Override
    protected Page<ClienteJuridicoListResponse> findAll(Pageable pageable) {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            return service.search(searchQuery.trim(), pageable);
        }
        return service.findAll(pageable);
    }

    @Override
    protected long count() {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            return service.countSearch(searchQuery.trim());
        }
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
