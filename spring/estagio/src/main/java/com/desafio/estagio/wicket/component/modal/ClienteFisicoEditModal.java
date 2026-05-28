package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoUpdateRequest;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.validation.ValidationConstants;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.model.ClienteFisicoUpdateFormModel;
import com.desafio.estagio.wicket.util.ErrorHandler;
import com.desafio.estagio.wicket.util.JavaScriptUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;

import java.io.Serial;

public class ClienteFisicoEditModal extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    public ClienteFisicoEditModal(String id, Long clienteId) {
        super(id);

        IModel<ClienteFisicoUpdateFormModel> detachedModel = new LoadableDetachableModel<>() {
            @Override
            protected ClienteFisicoUpdateFormModel load() {
                return new ClienteFisicoUpdateFormModel(clienteFisicoService.findById(clienteId));
            }
        };

        Form<ClienteFisicoUpdateFormModel> form = new Form<>("form", new CompoundPropertyModel<>(detachedModel));
        form.setOutputMarkupId(true);

        TextField<String> nomeField = new TextField<>("nome", String.class);
        nomeField.setRequired(true);
        nomeField.add(StringValidator.lengthBetween(ValidationConstants.NOME_MIN, ValidationConstants.NOME_MAX));
        Label nomeFeedback = ValidationFeedback.createFeedbackLabel("nomeFeedback", nomeField);
        ValidationFeedback.attachRealTimeValidation(nomeField, nomeFeedback);
        form.add(nomeField);
        form.add(nomeFeedback);

        TextField<String> emailField = new TextField<>("email", String.class);
        emailField.add(EmailAddressValidator.getInstance());
        emailField.add(StringValidator.maximumLength(ValidationConstants.EMAIL_MAX));
        Label emailFeedback = ValidationFeedback.createFeedbackLabel("emailFeedback", emailField);
        ValidationFeedback.attachRealTimeValidation(emailField, emailFeedback);
        form.add(emailField);
        form.add(emailFeedback);

        AjaxButton submit = new AjaxButton("submit", form) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ClienteFisicoUpdateFormModel model = (ClienteFisicoUpdateFormModel) form.getModelObject();
                Boolean success = ErrorHandler.handleServiceCall(() -> {
                    ClienteFisicoUpdateRequest updateRequest = new ClienteFisicoUpdateRequest(
                            model.getNome(), model.getEmail(), model.getEstaAtivo()
                    );
                    clienteFisicoService.update(model.getId(), updateRequest);
                    return true;
                }, target);
                if (Boolean.TRUE.equals(success)) {
                    ValidationFeedback.showToast(target, "success", "Cliente atualizado com sucesso!");
                    JavaScriptUtils.hideModalAndRemove(target, "editClienteFisicoModal");
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                ValidationFeedback.handleFormError(target, form);
            }
        };
        form.add(submit);

        AjaxLink<Void> cancelarBtn = new AjaxLink<>("cancelarBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                JavaScriptUtils.hideModalAndRemove(target, "editClienteFisicoModal");
            }
        };
        form.add(cancelarBtn);

        add(form);
    }
}
