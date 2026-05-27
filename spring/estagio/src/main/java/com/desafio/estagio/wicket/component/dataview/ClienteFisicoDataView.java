package com.desafio.estagio.wicket.component.dataview;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.component.modal.ClienteFisicoEditModal;
import com.desafio.estagio.wicket.util.ErrorHandler;
import com.desafio.estagio.wicket.component.table.ClientesFisicosTablePanel;
import com.desafio.estagio.wicket.util.JavaScriptUtils;
import com.desafio.estagio.wicket.page.clientes.ClienteFisicoDetalhePage;
import com.desafio.estagio.wicket.builder.AttributeModifierBuilder;
import com.desafio.estagio.wicket.builder.ComponentAttributeBuilder;
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

public class ClienteFisicoDataView extends AbstractClienteDataView<ClienteFisicoListResponse> {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    public ClienteFisicoDataView(String id, IDataProvider<ClienteFisicoListResponse> dataProvider, long itemsPerPage) {
        super(id, dataProvider, itemsPerPage);
    }

    @Override
    protected void populateRow(Item<ClienteFisicoListResponse> item) {
        ClienteFisicoListResponse cliente = item.getModelObject();

        WebMarkupContainer row = new WebMarkupContainer("editarForm");
        ComponentAttributeBuilder.of(row).setOutputMarkupId(true).build();

        row.add(new Label("id", new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return cliente.id() != null ? cliente.id().toString() : "";
            }
        }));
        row.add(new Label("nome", new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return cliente.nome() != null ? cliente.nome() : "";
            }
        }));
        row.add(new Label("cpf", new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return cliente.cpf() != null ? cliente.cpf() : "";
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
                Long id = cliente.id();
                ErrorHandler.handleServiceCall(target, null, () -> {
                    if (Boolean.TRUE.equals(cliente.estaAtivo())) {
                        clienteFisicoService.inactivate(id);
                        ValidationFeedback.showToast(target, "success", "Cliente inativado com sucesso!");
                    } else {
                        clienteFisicoService.activate(id);
                        ValidationFeedback.showToast(target, "success", "Cliente ativado com sucesso!");
                    }
                });
                findParent(ClientesFisicosTablePanel.class).refreshTable(target);
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
                ClienteFisicoDetalhePage.class,
                new PageParameters().set("clienteId", cliente.id()));
        AttributeModifierBuilder.on(detalhesBtn).custom("class", "btn btn-sm btn-outline-info rounded-circle p-1").build();
        row.add(detalhesBtn);

        AjaxLink<Void> editarBtn = new AjaxLink<>("editarBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                ClientesFisicosTablePanel tablePanel = findParent(ClientesFisicosTablePanel.class);
                tablePanel.refreshTable(target);
                WebMarkupContainer container = tablePanel.getEditModalContainer();
                ClienteFisicoEditModal editModal = new ClienteFisicoEditModal("editModal", cliente.id());
                container.addOrReplace(editModal);
                target.add(container);
                JavaScriptUtils.showBootstrapModal(target, "editClienteFisicoModal");
                JavaScriptUtils.reloadLucideIconsSafe(target);
            }
        };
        ComponentAttributeBuilder.of(editarBtn).setOutputMarkupId(true).build();
        row.add(editarBtn);

        item.add(row);
    }
}
