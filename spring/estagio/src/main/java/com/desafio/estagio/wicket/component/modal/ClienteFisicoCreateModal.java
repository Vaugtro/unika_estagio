package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.model.ClienteFisicoCreateFormModel;
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

public class ClienteFisicoCreateModal extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    public ClienteFisicoCreateModal(String id) {
        super(id);
        
        Form<ClienteFisicoCreateFormModel> form = new Form<>("form", new CompoundPropertyModel<>(new ClienteFisicoCreateFormModel()));
        form.setOutputMarkupId(true);
        
        TextField<String> cpfField = new TextField<>("cpf", String.class);
        cpfField.setRequired(true);
        cpfField.add(StringValidator.lengthBetween(11, 14));
        cpfField.add(new AttributeModifier("placeholder", "000.000.000-00"));
        cpfField.add(new AttributeModifier("data-mask", "000.000.000-00"));
        Label cpfFeedback = ValidationFeedback.createFeedbackLabel("cpfFeedback", cpfField);
        ValidationFeedback.attachRealTimeValidation(cpfField, cpfFeedback);
        form.add(cpfField);
        form.add(cpfFeedback);

        TextField<String> nomeField = new TextField<>("nome", String.class);
        nomeField.setRequired(true);
        nomeField.add(StringValidator.lengthBetween(3, 150));
        nomeField.add(new AttributeModifier("placeholder", "Nome completo"));
        Label nomeFeedback = ValidationFeedback.createFeedbackLabel("nomeFeedback", nomeField);
        ValidationFeedback.attachRealTimeValidation(nomeField, nomeFeedback);
        form.add(nomeField);
        form.add(nomeFeedback);

        TextField<String> rgField = new TextField<>("rg", String.class);
        rgField.setRequired(true);
        rgField.add(new AttributeModifier("placeholder", "RG"));
        Label rgFeedback = ValidationFeedback.createFeedbackLabel("rgFeedback", rgField);
        ValidationFeedback.attachRealTimeValidation(rgField, rgFeedback);
        form.add(rgField);
        form.add(rgFeedback);

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
                ClienteFisicoCreateFormModel model = (ClienteFisicoCreateFormModel) form.getModelObject();
                // TODO: Map to DTO and call clienteFisicoService.create(...)
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
