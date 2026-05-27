package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.wicket.mapper.ClienteJuridicoDtoMapper;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.validation.ValidationConstants;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.model.ClienteJuridicoUpdateFormModel;
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

public class ClienteJuridicoEditModal extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteJuridicoService clienteJuridicoService;

    public ClienteJuridicoEditModal(String id, Long clienteId) {
        super(id);

        IModel<ClienteJuridicoUpdateFormModel> detachedModel = new LoadableDetachableModel<>() {
            @Override
            protected ClienteJuridicoUpdateFormModel load() {
                return new ClienteJuridicoUpdateFormModel(clienteJuridicoService.findById(clienteId));
            }
        };

        Form<ClienteJuridicoUpdateFormModel> form = new Form<>("form", new CompoundPropertyModel<>(detachedModel));
        form.setOutputMarkupId(true);

        TextField<String> razaoSocialField = new TextField<>("razaoSocial", String.class);
        razaoSocialField.setRequired(true);
        razaoSocialField.add(StringValidator.lengthBetween(ValidationConstants.RAZAO_SOCIAL_MIN, ValidationConstants.RAZAO_SOCIAL_MAX));
        Label razaoSocialFeedback = ValidationFeedback.createFeedbackLabel("razaoSocialFeedback", razaoSocialField);
        ValidationFeedback.attachRealTimeValidation(razaoSocialField, razaoSocialFeedback);
        form.add(razaoSocialField);
        form.add(razaoSocialFeedback);

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
                ClienteJuridicoUpdateFormModel model = (ClienteJuridicoUpdateFormModel) form.getModelObject();
                ErrorHandler.handleServiceCall(target, form, () -> {
                    clienteJuridicoService.update(model.getId(), ClienteJuridicoDtoMapper.toUpdateRequest(model));
                    ValidationFeedback.showToast(target, "success", "Cliente atualizado com sucesso!");
                    JavaScriptUtils.hideAndRemoveBootstrapModal(target, "editClienteJuridicoModal");
                });
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
                JavaScriptUtils.hideAndRemoveBootstrapModal(target, "editClienteJuridicoModal");
            }
        };
        form.add(cancelarBtn);

        add(form);
    }
}
