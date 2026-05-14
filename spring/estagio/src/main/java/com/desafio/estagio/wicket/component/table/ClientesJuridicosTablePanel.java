package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoListResponse;
import com.desafio.estagio.service.ClienteJuridicoService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.devutils.DevUtilsPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.util.Iterator;

public class ClientesJuridicosTablePanel extends DevUtilsPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private transient ClienteJuridicoService clienteJuridicoService;

    public ClientesJuridicosTablePanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        WebMarkupContainer tableContainer = new WebMarkupContainer("tableContainer");
        tableContainer.setOutputMarkupId(true);
        add(tableContainer);

        IDataProvider<ClienteJuridicoListResponse> dataProvider = new ClienteJuridicoDataProvider();

        DataView<ClienteJuridicoListResponse> dataView = new DataView<>("rows", dataProvider, 10) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ClienteJuridicoListResponse> item) {
                ClienteJuridicoListResponse cliente = item.getModelObject();

                item.add(new Label("id", String.valueOf(cliente.id())));
                item.add(new Label("razaoSocial", cliente.razaoSocial()));
                item.add(new Label("cnpj", cliente.cnpj()));
                item.add(new Label("email", cliente.email() != null ? cliente.email() : "-"));

                Label status = new Label("status", cliente.estaAtivo() ? "Ativo" : "Inativo");
                status.add(new AttributeModifier("class",
                        cliente.estaAtivo() ? "badge bg-success" : "badge bg-danger"));
                item.add(status);

                Form<Void> editarForm = new Form<>("editarBtnForm");
                editarForm.add(new AjaxButton("editarBtn", editarForm) {
                    @Serial
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        System.out.println("HAROU SUBMIT");
                    }
                });
                item.add(editarForm);
            }
        };

        tableContainer.add(dataView);
        add(new PagingNavigator("navigator", dataView));
    }

    private class ClienteJuridicoDataProvider implements IDataProvider<ClienteJuridicoListResponse> {
        @Serial
        private static final long serialVersionUID = 1L;

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

            return new LoadableDetachableModel<>() {
                @Serial
                private static final long serialVersionUID = 1L;

                @Override
                protected ClienteJuridicoListResponse load() {
                    return ClientesJuridicosTablePanel.this.clienteJuridicoService.findByIdList(id);
                }
            };
        }

        @Override
        public void detach() {
        }
    }
}