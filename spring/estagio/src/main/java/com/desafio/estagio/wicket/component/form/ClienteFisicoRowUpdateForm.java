package com.desafio.estagio.wicket.component.form;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoUpdateRequest;
import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.validation.ValidationConstants;
import com.desafio.estagio.wicket.model.ClienteFisicoUpdateFormModel;
import lombok.Getter;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;

import java.io.Serial;

public class ClienteFisicoRowUpdateForm extends Form<ClienteFisicoUpdateFormModel> {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    @Getter
    private final Item<ClienteFisicoListResponse> parentItem;

    public ClienteFisicoRowUpdateForm(String id, ClienteFisicoListResponse cliente, Item<ClienteFisicoListResponse> parentItem) {
        super(id);
        this.parentItem = parentItem;
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);

        IModel<ClienteFisicoUpdateFormModel> detachedModel = new LoadableDetachableModel<>() {
            @Override
            protected ClienteFisicoUpdateFormModel load() {
                return new ClienteFisicoUpdateFormModel(clienteFisicoService.findById(cliente.id()));
            }
        };

        setModel(new CompoundPropertyModel<>(detachedModel));

        add(new Label("id"));
        add(new Label("cpf"));

        TextField<String> nomeField = new TextField<>("nome");
        nomeField.setRequired(true);
        nomeField.add(StringValidator.lengthBetween(ValidationConstants.NOME_MIN, ValidationConstants.NOME_MAX));
        nomeField.setOutputMarkupId(true);
        nomeField.add(new AttributeAppender("class", new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;
            @Override
            public String getObject() {
                return !nomeField.getFeedbackMessages().isEmpty() ? " is-invalid" : "";
            }
        }));
        add(nomeField);

        TextField<String> emailField = new TextField<>("email");
        emailField.add(EmailAddressValidator.getInstance());
        emailField.add(StringValidator.maximumLength(150));
        emailField.setOutputMarkupId(true);
        emailField.add(new AttributeAppender("class", new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;
            @Override
            public String getObject() {
                return !emailField.getFeedbackMessages().isEmpty() ? " is-invalid" : "";
            }
        }));
        add(emailField);

        IModel<String> statusTextModel = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                ClienteFisicoUpdateFormModel model = ClienteFisicoRowUpdateForm.this.getModelObject();
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
                ClienteFisicoUpdateFormModel model = ClienteFisicoRowUpdateForm.this.getModelObject();
                boolean isAtivo = model != null && model.getEstaAtivo();
                return isAtivo ? "btn btn-sm btn-success" : "btn btn-sm btn-danger";
            }
        }));

        toggleBtn.add(new AttributeModifier("title", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                ClienteFisicoUpdateFormModel model = ClienteFisicoRowUpdateForm.this.getModelObject();
                boolean isAtivo = model != null && model.getEstaAtivo();
                return isAtivo ? "Inativar" : "Ativar";
            }
        }));

        add(toggleBtn);

        AjaxButton editButton = getEditButton();
        add(editButton);
    }

    private AjaxButton getEditButton() {
        AjaxButton editButton = new AjaxButton("editarBtn", this) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    ClienteFisicoUpdateFormModel model = (ClienteFisicoUpdateFormModel) form.getModelObject();
                    if (model == null) {
                        showToast(target, "error", "Modelo de dados não encontrado");
                        return;
                    }
                    ClienteFisicoUpdateRequest updateRequest = new ClienteFisicoUpdateRequest(
                            model.getNome(), model.getEmail(), model.getEstaAtivo()
                    );
                    clienteFisicoService.update(model.getId(), updateRequest);

                    form.setDefaultModelObject(new ClienteFisicoUpdateFormModel(
                            clienteFisicoService.findById(model.getId())));

                    showToast(target, "success", "Cliente atualizado com sucesso!");
                    target.add(form);
                    target.appendJavaScript("if(typeof lucide !== 'undefined') lucide.createIcons();");

                } catch (BusinessException e) {
                    showToast(target, "error", e.getMessage());
                } catch (Exception e) {
                    showToast(target, "error", "Erro ao atualizar cliente: " + e.getMessage());
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(form);
            }
        };

        editButton.setOutputMarkupId(true);
        editButton.setDefaultFormProcessing(true);
        return editButton;
    }

    private void showToast(AjaxRequestTarget target, String type, String message) {
        String escapedMessage = message
                .replace("\\", "\\\\").replace("'", "\\'")
                .replace("\"", "\\\"").replace("\n", "\\n")
                .replace("\r", "\\r");
        target.appendJavaScript(String.format(
                "if (typeof window.showToast === 'function') { window.showToast('%s', '%s'); }" +
                " else { console.error('showToast function not found'); alert('%s'); }",
                type, escapedMessage, escapedMessage));
    }

    private AjaxLink<Void> getToggleBtn() {
        return new AjaxLink<>("statusBtn") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ClienteFisicoUpdateFormModel model = ClienteFisicoRowUpdateForm.this.getModelObject();
                boolean newStatus = !model.getEstaAtivo();
                if (model.getEstaAtivo()) {
                    clienteFisicoService.inactivate(model.getId());
                } else {
                    clienteFisicoService.activate(model.getId());
                }
                model.setEstaAtivo(newStatus);
                target.add(ClienteFisicoRowUpdateForm.this);
                target.appendJavaScript("if(typeof lucide !== 'undefined') lucide.createIcons();");
            }
        };
    }
}
