package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.model.ClienteJuridicoCreateFormModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import java.io.Serial;

public class ClienteJuridicoCreateModal extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteJuridicoService clienteJuridicoService;

    public ClienteJuridicoCreateModal(String id) {
        super(id);
        
        Form<ClienteJuridicoCreateFormModel> form = new Form<>("form", new CompoundPropertyModel<>(new ClienteJuridicoCreateFormModel()));
        form.setOutputMarkupId(true);
        
        TextField<String> cnpjField = new TextField<>("cnpj", String.class);
        cnpjField.setRequired(true);
        cnpjField.add(StringValidator.lengthBetween(14, 18));
        cnpjField.add(new AttributeModifier("placeholder", "00.000.000/0000-00"));
        cnpjField.add(new AttributeModifier("data-mask", "00.000.000/0000-00"));
        Label cnpjFeedback = ValidationFeedback.createFeedbackLabel("cnpjFeedback", cnpjField);
        ValidationFeedback.attachRealTimeValidation(cnpjField, cnpjFeedback);
        form.add(cnpjField);
        form.add(cnpjFeedback);

        TextField<String> razaoSocialField = new TextField<>("razaoSocial", String.class);
        razaoSocialField.setRequired(true);
        razaoSocialField.add(StringValidator.lengthBetween(3, 150));
        razaoSocialField.add(new AttributeModifier("placeholder", "Razão Social"));
        Label razaoSocialFeedback = ValidationFeedback.createFeedbackLabel("razaoSocialFeedback", razaoSocialField);
        ValidationFeedback.attachRealTimeValidation(razaoSocialField, razaoSocialFeedback);
        form.add(razaoSocialField);
        form.add(razaoSocialFeedback);

        TextField<String> ieField = new TextField<>("inscricaoEstadual", String.class);
        ieField.setRequired(true);
        ieField.add(new AttributeModifier("placeholder", "Inscrição Estadual"));
        Label ieFeedback = ValidationFeedback.createFeedbackLabel("ieFeedback", ieField);
        ValidationFeedback.attachRealTimeValidation(ieField, ieFeedback);
        form.add(ieField);
        form.add(ieFeedback);

        TextField<String> emailField = new TextField<>("email", String.class);
        emailField.add(new AttributeModifier("placeholder", "E-mail"));
        Label emailFeedback = ValidationFeedback.createFeedbackLabel("emailFeedback", emailField);
        ValidationFeedback.attachRealTimeValidation(emailField, emailFeedback);
        form.add(emailField);
        form.add(emailFeedback);

        form.add(new AjaxButton("submit", form) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ClienteJuridicoCreateFormModel model = (ClienteJuridicoCreateFormModel) form.getModelObject();
                // TODO: Map to DTO and call clienteJuridicoService.create(...)
                ValidationFeedback.showToast(target, "success", "Cliente criado com sucesso!");
                target.add(form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(form);
                StringBuilder errors = new StringBuilder();
                form.getFeedbackMessages().messages(msg -> msg.getLevel() == org.apache.wicket.feedback.FeedbackMessage.ERROR)
                        .forEach(msg -> {
                            if (!errors.isEmpty()) errors.append("<br>");
                            errors.append(msg.getMessage());
                        });
                if (!errors.isEmpty()) {
                    ValidationFeedback.showToast(target, "error", errors.toString());
                }
            }
        });
        
        add(form);
    }
}
