package com.desafio.estagio.wicket.provider;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.service.ClienteFisicoService;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.util.Iterator;

public class ClienteFisicoDataProvider implements IDataProvider<ClienteFisicoListResponse> {
    @Serial
    private static final long serialVersionUID = 1L;

    private final ClienteFisicoService clienteFisicoService;

    public ClienteFisicoDataProvider(ClienteFisicoService clienteFisicoService) {
        this.clienteFisicoService = clienteFisicoService;
    }


    @Override
    public Iterator<? extends ClienteFisicoListResponse> iterator(long first, long count) {
        Pageable pageable = PageRequest.of(
                (int) (first / count), (int) count, Sort.by("id").ascending());
        return clienteFisicoService.findAll(pageable).getContent().iterator();
    }

    @Override
    public long size() {
        return clienteFisicoService.count();
    }

    @Override
    public IModel<ClienteFisicoListResponse> model(ClienteFisicoListResponse object) {
        final Long id = object.id();
        return new LoadableDetachableModel<ClienteFisicoListResponse>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected ClienteFisicoListResponse load() {
                return clienteFisicoService.findByIdList(id);
            }
        };
    }

    @Override
    public void detach() {
    }
}