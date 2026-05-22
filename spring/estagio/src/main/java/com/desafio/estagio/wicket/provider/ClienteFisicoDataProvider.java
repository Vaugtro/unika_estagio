package com.desafio.estagio.wicket.provider;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.service.ClienteFisicoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serial;

public class ClienteFisicoDataProvider extends AbstractClienteDataProvider<ClienteFisicoListResponse> {
    @Serial
    private static final long serialVersionUID = 1L;

    private final ClienteFisicoService service;

    private String searchQuery;

    public ClienteFisicoDataProvider(ClienteFisicoService service) {
        this.service = service;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    @Override
    protected Page<ClienteFisicoListResponse> findAll(Pageable pageable) {
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
    protected ClienteFisicoListResponse findByIdList(Long id) {
        return service.findByIdList(id);
    }

    @Override
    protected Long extractId(ClienteFisicoListResponse object) {
        return object.id();
    }
}
