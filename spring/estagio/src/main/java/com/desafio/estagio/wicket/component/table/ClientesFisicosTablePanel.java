package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.service.ClienteFisicoService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.devutils.DevUtilsPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
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

public class ClientesFisicosTablePanel extends DevUtilsPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private transient ClienteFisicoService clienteFisicoService;

    public ClientesFisicosTablePanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        WebMarkupContainer tableContainer = new WebMarkupContainer("tableContainer");
        tableContainer.setOutputMarkupId(true);
        add(tableContainer);

        IDataProvider<ClienteFisicoListResponse> dataProvider = new ClienteFisicoDataProvider();

        DataView<ClienteFisicoListResponse> dataView = new DataView<>("rows", dataProvider, 10) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ClienteFisicoListResponse> item) {
                ClienteFisicoListResponse cliente = item.getModelObject();

                item.add(new Label("id", String.valueOf(cliente.id())));
                item.add(new Label("nome", cliente.nome()));
                item.add(new Label("cpf", cliente.cpf()));
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
        //add(new AjaxPagingNavigation("navigator", dataView));
    }

    private class ClienteFisicoDataProvider implements IDataProvider<ClienteFisicoListResponse> {
        @Serial
        private static final long serialVersionUID = 1L;

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

            return new LoadableDetachableModel<>() {
                @Serial
                private static final long serialVersionUID = 1L;

                @Override
                protected ClienteFisicoListResponse load() {
                    return ClientesFisicosTablePanel.this.clienteFisicoService.findByIdList(id);
                }
            };
        }

        @Override
        public void detach() {
        }
    }
}