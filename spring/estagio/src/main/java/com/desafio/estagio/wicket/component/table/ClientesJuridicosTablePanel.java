package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoListResponse;
import com.desafio.estagio.service.ClienteJuridicoService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.util.Iterator;

public class ClientesJuridicosTablePanel extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteJuridicoService clienteJuridicoService;

    private WebMarkupContainer tableContainer;

    public ClientesJuridicosTablePanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Table Container
        tableContainer = new WebMarkupContainer("tableContainer");
        tableContainer.setOutputMarkupId(true);
        add(tableContainer);

        // Data Provider
        IDataProvider<ClienteJuridicoListResponse> dataProvider = new IDataProvider<>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public Iterator<? extends ClienteJuridicoListResponse> iterator(long first, long count) {
                Pageable pageable = PageRequest.of((int) (first / count), (int) count, Sort.by("id").ascending());
                Page<ClienteJuridicoListResponse> page = clienteJuridicoService.findAll(pageable);
                return page.getContent().iterator();
            }

            @Override
            public long size() {
                return clienteJuridicoService.count();
            }

            @Override
            public IModel<ClienteJuridicoListResponse> model(ClienteJuridicoListResponse object) {
                return new CompoundPropertyModel<>(Model.of(object));
            }

            @Override
            public void detach() {
                // Do nothing
            }
        };

        // Data View
        // Basic fields
        // Status badge
        DataView<ClienteJuridicoListResponse> dataView = new DataView<>("rows", dataProvider, 10) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ClienteJuridicoListResponse> item) {
                ClienteJuridicoListResponse cliente = item.getModelObject();

                // Basic fields
                item.add(new Label("id", String.valueOf(cliente.id())));
                item.add(new Label("razaoSocial", cliente.razaoSocial()));
                item.add(new Label("cnpj", cliente.cnpj()));
                item.add(new Label("email", cliente.email() != null ? cliente.email() : "-"));

                // Status badge
                Label status = new Label("status", cliente.estaAtivo() ? "Ativo" : "Inativo");
                status.add(new AttributeModifier("class", cliente.estaAtivo() ? "badge bg-success" : "badge bg-danger"));
                item.add(status);
            }
        };

        tableContainer.add(dataView);

        // Paginator
        PagingNavigator navigator = new PagingNavigator("navigator", dataView);
        add(navigator);
    }

    public void refresh(AjaxRequestTarget target) {
        target.add(tableContainer);
    }
}