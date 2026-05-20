package com.desafio.estagio.wicket.component.form;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoListResponse;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoUpdateRequest;
import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.wicket.model.ClienteJuridicoModel;
import lombok.Getter;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jspecify.annotations.NonNull;

import java.io.Serial;

public class ClienteJuridicoRowUpdateForm extends Form<ClienteJuridicoModel> {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteJuridicoService clienteJuridicoService;

    @Getter
    private final Item<ClienteJuridicoListResponse> parentItem;

    public ClienteJuridicoRowUpdateForm(String id, ClienteJuridicoListResponse cliente, Item<ClienteJuridicoListResponse> parentItem) {
        super(id);
        this.parentItem = parentItem;
        this.setOutputMarkupId(true);
        this.setOutputMarkupPlaceholderTag(true);

        IModel<ClienteJuridicoModel> detachedModel = new LoadableDetachableModel<>() {
            @Override
            protected ClienteJuridicoModel load() {
                return new ClienteJuridicoModel(clienteJuridicoService.findById(cliente.id()));
            }
        };

        this.setModel(new CompoundPropertyModel<>(detachedModel));

        this.add(new Label("id"));
        this.add(new Label("cnpj"));
        this.add(new TextField<>("razaoSocial"));
        this.add(new TextField<>("email"));

        IModel<String> statusTextModel = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                ClienteJuridicoModel model = ClienteJuridicoRowUpdateForm.this.getModelObject();
                return model != null && model.getEstaAtivo() ? "Ativo" : "Inativo";
            }
        };

        Label btnStatus = new Label("status", statusTextModel);
        btnStatus.setOutputMarkupId(true);

        AjaxLink<Void> toggleBtn = getToggleBtn();
        toggleBtn.add(btnStatus);

        toggleBtn.add(new AttributeModifier("class", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                ClienteJuridicoModel model = ClienteJuridicoRowUpdateForm.this.getModelObject();
                boolean isAtivo = model != null && model.getEstaAtivo();
                return isAtivo ? "btn btn-sm btn-success" : "btn btn-sm btn-danger";
            }
        }));

        toggleBtn.add(new AttributeModifier("title", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                ClienteJuridicoModel model = ClienteJuridicoRowUpdateForm.this.getModelObject();
                boolean isAtivo = model != null && model.getEstaAtivo();
                return isAtivo ? "Inativar" : "Ativar";
            }
        }));

        this.add(toggleBtn);

        AjaxButton editButton = getEditButton();
        this.add(editButton);
    }

    private @NonNull AjaxButton getEditButton() {
        AjaxButton editButton = new AjaxButton("editarBtn", this) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    ClienteJuridicoModel model = (ClienteJuridicoModel) form.getModelObject();

                    if (model == null) {
                        showToast(target, "error", "Modelo de dados não encontrado");
                        return;
                    }

                    ClienteJuridicoUpdateRequest updateRequest = new ClienteJuridicoUpdateRequest(
                            model.getRazaoSocial(),
                            model.getInscricaoEstadual(),
                            model.getEmail(),
                            model.getDataCriacaoEmpresa(),
                            model.getEstaAtivo(),
                            null // Endereços não são atualizados neste formulário simples
                    );

                    clienteJuridicoService.update(model.getId(), updateRequest);

                    form.setDefaultModelObject(new ClienteJuridicoModel(clienteJuridicoService.findById(model.getId())));

                    showToast(target, "success", "Cliente atualizado com sucesso!");

                    target.add(form);
                    target.appendJavaScript("if(typeof lucide !== 'undefined') lucide.createIcons();");

                } catch (BusinessException e) {
                    showToast(target, "error", e.getMessage());
                } catch (Exception e) {
                    showToast(target, "error", "Erro ao atualizar cliente: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(form);
                StringBuilder errors = new StringBuilder();
                form.getFeedbackMessages().messages(new IFeedbackMessageFilter() {
                    @Override
                    public boolean accept(FeedbackMessage message) {
                        return message.getLevel() == FeedbackMessage.ERROR;
                    }
                }).forEach(msg -> {
                    if (!errors.isEmpty()) errors.append("<br>");
                    errors.append(msg.getMessage().toString());
                });

                if (!errors.isEmpty()) {
                    showToast(target, "error", errors.toString());
                }
            }
        };

        editButton.setOutputMarkupId(true);
        editButton.setDefaultFormProcessing(true);
        return editButton;
    }

    void showToast(AjaxRequestTarget target, String type, String message) {
        String escapedMessage = message
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");

        String toastScript = String.format(
                "if (typeof window.showToast === 'function') {" +
                        "  window.showToast('%s', '%s');" +
                        "} else {" +
                        "  console.error('showToast function not found');" +
                        "  alert('%s');" +
                        "}",
                type, escapedMessage, escapedMessage
        );

        target.appendJavaScript(toastScript);
    }

    private AjaxLink<Void> getToggleBtn() {
        return new AjaxLink<>("statusBtn") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ClienteJuridicoModel model = ClienteJuridicoRowUpdateForm.this.getModelObject();
                boolean newStatus = !model.getEstaAtivo();

                if (model.getEstaAtivo()) {
                    clienteJuridicoService.inactivate(model.getId());
                } else {
                    clienteJuridicoService.activate(model.getId());
                }

                target.appendJavaScript(
                        "var btn = document.getElementById('" + getMarkupId() + "');" +
                                "btn.className = '" + (newStatus ? "btn btn-sm btn-success" : "btn btn-sm btn-danger") + "';" +
                                "btn.title = '" + (newStatus ? "Inativar" : "Ativar") + "';" +
                                "btn.querySelector('span').textContent = '" + (newStatus ? "Ativo" : "Inativo") + "';"
                );
            }
        };
    }
}
