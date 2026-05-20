package com.desafio.estagio.wicket.component.form;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoUpdateRequest;
import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.model.ClienteFisicoModel;
import jakarta.validation.Valid;
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

public class ClienteFisicoRowUpdateForm extends Form<ClienteFisicoModel> {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    @Getter
    private final Item<ClienteFisicoListResponse> parentItem;

    public ClienteFisicoRowUpdateForm(String id, ClienteFisicoListResponse cliente, Item<ClienteFisicoListResponse> parentItem) {
        super(id);
        this.parentItem = parentItem;
        this.setOutputMarkupId(true);
        this.setOutputMarkupPlaceholderTag(true);

        IModel<ClienteFisicoModel> detachedModel = new LoadableDetachableModel<>() {
            @Override
            protected ClienteFisicoModel load() {
                return new ClienteFisicoModel(clienteFisicoService.findById(cliente.id()));
            }
        };

        this.setModel(new CompoundPropertyModel<>(detachedModel));

        this.add(new Label("id"));
        this.add(new Label("cpf"));
        this.add(new TextField<>("nome"));
        this.add(new TextField<>("email"));

        // Cleaned up model tracking logic using a clean ReadOnly Model
        IModel<String> statusTextModel = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                ClienteFisicoModel model = ClienteFisicoRowUpdateForm.this.getModelObject();
                return model != null && model.getEstaAtivo() ? "Ativo" : "Inativo";
            }
        };

        Label btnStatus = new Label("status", statusTextModel);
        btnStatus.setOutputMarkupId(true);

        AjaxLink<Void> toggleBtn = getToggleBtn();
        toggleBtn.add(btnStatus);

        // Dynamic class based on current model state
        toggleBtn.add(new AttributeModifier("class", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                ClienteFisicoModel model = ClienteFisicoRowUpdateForm.this.getModelObject();
                boolean isAtivo = model != null && model.getEstaAtivo();
                return isAtivo ? "btn btn-sm btn-success" : "btn btn-sm btn-danger";
            }
        }));

        // Dynamic title based on current model state
        toggleBtn.add(new AttributeModifier("title", new org.apache.wicket.model.AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                ClienteFisicoModel model = ClienteFisicoRowUpdateForm.this.getModelObject();
                boolean isAtivo = model != null && model.getEstaAtivo();
                return isAtivo ? "Inativar" : "Ativar";
            }
        }));

        this.add(toggleBtn);

        // Store reference to edit button
        // Force refresh: explicitly clear model or re-bind
        // Refresh the entire form to update all components
        // Re-attach any JavaScript initializations if needed
        // Show validation errors as toast
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
                    ClienteFisicoModel model = (ClienteFisicoModel) form.getModelObject();

                    if (model == null) {
                        showToast(target, "error", "Modelo de dados não encontrado");
                        return;
                    }

                    ClienteFisicoUpdateRequest updateRequest = new ClienteFisicoUpdateRequest(
                            model.getNome(),
                            model.getEmail(),
                            model.getEstaAtivo()
                    );

                    clienteFisicoService.update(model.getId(), updateRequest);

                    // Force refresh: explicitly clear model or re-bind
                    form.setDefaultModelObject(new ClienteFisicoModel(clienteFisicoService.findById(model.getId())));

                    showToast(target, "success", "Cliente atualizado com sucesso!");

                    // Refresh the entire form to update all components
                    target.add(form);

                    // Re-attach any JavaScript initializations if needed
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
                // Show validation errors as toast
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
        // Escape the message for JavaScript
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
                ClienteFisicoModel model = ClienteFisicoRowUpdateForm.this.getModelObject();
                boolean newStatus = !model.getEstaAtivo();

                if (model.getEstaAtivo()) {
                    clienteFisicoService.inactivate(model.getId());
                } else {
                    clienteFisicoService.activate(model.getId());
                }

                // Skip target.add entirely, just update DOM directly
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