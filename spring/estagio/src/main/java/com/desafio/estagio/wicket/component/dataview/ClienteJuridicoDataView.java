package com.desafio.estagio.wicket.component.dataview;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoListResponse;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.wicket.builder.AttributeModifierBuilder;
import com.desafio.estagio.wicket.builder.ComponentAttributeBuilder;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.component.modal.ClienteJuridicoEditModal;
import com.desafio.estagio.wicket.component.table.ClientesJuridicosTablePanel;
import com.desafio.estagio.wicket.page.clientes.ClienteJuridicoDetalhePage;
import com.desafio.estagio.wicket.util.ErrorHandler;
import com.desafio.estagio.wicket.util.JavaScriptUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serial;

public class ClienteJuridicoDataView extends AbstractClienteDataView<ClienteJuridicoListResponse> {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteJuridicoService clienteJuridicoService;

    public ClienteJuridicoDataView(String id, IDataProvider<ClienteJuridicoListResponse> dataProvider, long itemsPerPage) {
        super(id, dataProvider, itemsPerPage);
    }

    @Override
    protected void populateRow(Item<ClienteJuridicoListResponse> item) {
        ClienteJuridicoListResponse cliente = item.getModelObject();

        WebMarkupContainer row = ComponentAttributeBuilder.of(new WebMarkupContainer("editarForm"))
                .setOutputMarkupId(true)
                .build();

        row.add(new Label("id", new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return cliente.id() != null ? cliente.id().toString() : "";
            }
        }));
        row.add(new Label("razaoSocial", new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return cliente.razaoSocial() != null ? cliente.razaoSocial() : "";
            }
        }));
        row.add(new Label("cnpj", new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return cliente.cnpj() != null ? cliente.cnpj() : "";
            }
        }));
        row.add(new Label("email", new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return cliente.email() != null ? cliente.email() : "";
            }
        }));

        AjaxLink<Void> toggleBtn = new AjaxLink<>("toggleBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                ErrorHandler.handleServiceCall(() -> {
                    Long id = cliente.id();
                    if (Boolean.TRUE.equals(cliente.estaAtivo())) {
                        clienteJuridicoService.inactivate(id);
                        ValidationFeedback.showToast(target, "success", "Cliente inativado com sucesso!");
                    } else {
                        clienteJuridicoService.activate(id);
                        ValidationFeedback.showToast(target, "success", "Cliente ativado com sucesso!");
                    }
                }, target);
                findParent(ClientesJuridicosTablePanel.class).refreshTable(target);
            }
        };
        toggleBtn.add(new Label("statusBadge", new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return Boolean.TRUE.equals(cliente.estaAtivo()) ? "Ativo" : "Inativo";
            }
        }));
        toggleBtn.add(new AttributeModifier("class", new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return Boolean.TRUE.equals(cliente.estaAtivo()) ? "btn btn-sm btn-success" : "btn btn-sm btn-danger";
            }
        }));
        toggleBtn.add(new AttributeModifier("title", new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return Boolean.TRUE.equals(cliente.estaAtivo()) ? "Inativar" : "Ativar";
            }
        }));
        row.add(toggleBtn);

        BookmarkablePageLink<Void> detalhesBtn = new BookmarkablePageLink<>("detalhesBtn",
                ClienteJuridicoDetalhePage.class,
                new PageParameters().set("clienteId", cliente.id()));
        AttributeModifierBuilder.create().cssClass("btn btn-sm btn-outline-info rounded-circle p-1").buildAndAdd(detalhesBtn);
        row.add(detalhesBtn);

        AjaxLink<Void> editarBtn = new AjaxLink<>("editarBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                ClientesJuridicosTablePanel tablePanel = findParent(ClientesJuridicosTablePanel.class);
                tablePanel.refreshTable(target);
                WebMarkupContainer container = tablePanel.getEditModalContainer();
                ClienteJuridicoEditModal editModal = new ClienteJuridicoEditModal("editModal", cliente.id());
                container.addOrReplace(editModal);
                target.add(container);
                JavaScriptUtils.showModalWithIcons(target, "editClienteJuridicoModal");
            }
        };
        ComponentAttributeBuilder.of(editarBtn).setOutputMarkupId(true).build();
        row.add(editarBtn);

        item.add(row);
    }
}
