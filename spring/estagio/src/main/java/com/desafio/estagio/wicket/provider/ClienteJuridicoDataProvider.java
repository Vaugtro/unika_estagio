package com.desafio.estagio.wicket.provider;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoListResponse;
import com.desafio.estagio.service.ClienteJuridicoService;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.util.Iterator;

public class ClienteJuridicoDataProvider implements IDataProvider<ClienteJuridicoListResponse> {
    @Serial
    private static final long serialVersionUID = 1L;

    private final ClienteJuridicoService clienteJuridicoService;

    public ClienteJuridicoDataProvider(ClienteJuridicoService clienteJuridicoService) {
        this.clienteJuridicoService = clienteJuridicoService;
    }

    @Override
    public Iterator<? extends ClienteJuridicoListResponse> iterator(long first, long count) {
        Pageable pageable = PageRequest.of(
                (int) (first / count), (int) count, Sort.by("id").ascending());
        return clienteJuridicoService.findAll(pageable).getContent().iterator();
    }

    @Override
    public long size() {
        return clienteJuridicoService.count();
    }

    @Override
    public IModel<ClienteJuridicoListResponse> model(ClienteJuridicoListResponse object) {
        final Long id = object.id();
        return new LoadableDetachableModel<ClienteJuridicoListResponse>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected ClienteJuridicoListResponse load() {
                return clienteJuridicoService.findByIdList(id);
            }
        };
    }

    @Override
    public void detach() {
    }
}
