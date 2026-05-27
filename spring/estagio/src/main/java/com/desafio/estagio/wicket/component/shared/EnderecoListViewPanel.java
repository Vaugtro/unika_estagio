package com.desafio.estagio.wicket.component.shared;

import com.desafio.estagio.dto.endereco.EnderecoResponse;
import com.desafio.estagio.model.formatter.TelefoneFormatter;
import com.desafio.estagio.service.EnderecoService;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.mapper.EnderecoDtoMapper;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;
import com.desafio.estagio.wicket.util.ErrorHandler;
import com.desafio.estagio.wicket.util.JavaScriptUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import wicket.js.WicketJsAnchor;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class EnderecoListViewPanel extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final ResourceReference MASKS_JS = new JavaScriptResourceReference(WicketJsAnchor.class, "masks.js");
    private final Long clienteId;
    private final List<EnderecoCreateFormModel> modalEnderecos = new ArrayList<>();
    private final Form<?> modalForm;
    private final Label enderecoModalLabel;
    private final WebMarkupContainer enderecosContainer;

    @SpringBean
    private EnderecoService enderecoService;

    public EnderecoListViewPanel(String id, Long clienteId) {
        super(id);
        this.clienteId = clienteId;
        setOutputMarkupId(true);

        // --- Modal form ---
        modalForm = new Form<>("modalForm");
        modalForm.setOutputMarkupId(true);

        enderecoModalLabel = new Label("enderecoModalLabel", Model.of("Novo Endereço"));
        enderecoModalLabel.setOutputMarkupId(true);
        modalForm.add(enderecoModalLabel);

        modalForm.add(new EnderecoCreateTablePanel("enderecoTablePanel", modalEnderecos));

        add(modalForm);

        // --- Enderecos table ---
        enderecosContainer = new WebMarkupContainer("enderecosContainer");
        enderecosContainer.setOutputMarkupId(true);
        add(enderecosContainer);

        LoadableDetachableModel<List<EnderecoResponse>> enderecosModel = new LoadableDetachableModel<>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected List<EnderecoResponse> load() {
                return enderecoService.findAllByClienteId(clienteId);
            }
        };

        ListView<EnderecoResponse> enderecosView = new ListView<>("enderecoRow", enderecosModel) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<EnderecoResponse> item) {
                EnderecoResponse end = item.getModelObject();

                item.add(new Label("logradouro", end.logradouro() != null ? end.logradouro() : ""));
                item.add(new Label("numero", end.numero() != null ? end.numero().toString() : ""));
                item.add(new Label("bairro", end.bairro() != null ? end.bairro() : ""));
                item.add(new Label("cep", end.cep() != null ? end.cep() : ""));
                item.add(new Label("cidade", end.cidade() != null ? end.cidade() : ""));
                item.add(new Label("estado", end.estado() != null ? end.estado() : ""));
                item.add(new Label("telefone", end.telefone() != null
                        ? TelefoneFormatter.format(end.telefone()) : ""));

                item.add(new EnderecoRowActionPanel("rowActions", end, getList(),
                        enderecosContainer, modalEnderecos, modalForm, enderecoModalLabel));
            }
        };
        enderecosContainer.add(enderecosView);

        // --- Modal save button ---
        modalForm.add(new AjaxButton("salvarEnderecoBtn", modalForm) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (modalEnderecos.isEmpty()) return;

                EnderecoCreateFormModel endForm = modalEnderecos.get(0);

                ErrorHandler.handleServiceCall(target, form, () -> {
                    if (endForm.getId() != null) {
                        enderecoService.update(endForm.getId(),
                                EnderecoDtoMapper.toUpdateRequest(endForm));
                        ValidationFeedback.showToast(target, "success",
                                "Endereço atualizado com sucesso!");
                    } else {
                        enderecoService.create(
                                EnderecoDtoMapper.toCreateRequest(endForm, clienteId));
                        ValidationFeedback.showToast(target, "success",
                                "Endereço adicionado com sucesso!");
                    }

                    modalEnderecos.clear();
                    target.add(enderecosContainer);
                    target.add(modalForm);
                    target.appendJavaScript("fecharModalEndereco();");
                    JavaScriptUtils.reloadLucideIcons(target);
                });
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                ValidationFeedback.handleFormError(target, form);
                target.add(form);
            }
        });

        // --- Adicionar button ---
        add(new AjaxLink<Void>("adicionarEnderecoBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                modalEnderecos.clear();
                modalForm.getFeedbackMessages().clear();
                EnderecoCreateFormModel end = new EnderecoCreateFormModel();
                end.setPrincipal(false);
                modalEnderecos.add(end);
                enderecoModalLabel.setDefaultModelObject("Novo Endereço");
                target.add(modalForm);
                target.appendJavaScript("abrirModalEndereco();");
            }
        });

        // --- File operations ---
        add(new EnderecoFilePanel("filePanel", clienteId, enderecosContainer));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(MASKS_JS));
    }
}
