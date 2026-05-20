package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.model.ClienteFisicoCreateFormModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
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
        Label cpfFeedback = createFeedbackLabel("cpfFeedback", cpfField);
        addRealTimeValidation(cpfField, cpfFeedback);
        form.add(cpfField);
        form.add(cpfFeedback);

        TextField<String> nomeField = new TextField<>("nome", String.class);
        nomeField.setRequired(true);
        nomeField.add(StringValidator.lengthBetween(3, 150));
        nomeField.add(new AttributeModifier("placeholder", "Nome completo"));
        Label nomeFeedback = createFeedbackLabel("nomeFeedback", nomeField);
        addRealTimeValidation(nomeField, nomeFeedback);
        form.add(nomeField);
        form.add(nomeFeedback);

        TextField<String> rgField = new TextField<>("rg", String.class);
        rgField.setRequired(true);
        rgField.add(new AttributeModifier("placeholder", "RG"));
        Label rgFeedback = createFeedbackLabel("rgFeedback", rgField);
        addRealTimeValidation(rgField, rgFeedback);
        form.add(rgField);
        form.add(rgFeedback);

        TextField<String> emailField = new TextField<>("email", String.class);
        emailField.add(new AttributeModifier("placeholder", "E-mail"));
        Label emailFeedback = createFeedbackLabel("emailFeedback", emailField);
        addRealTimeValidation(emailField, emailFeedback);
        form.add(emailField);
        form.add(emailFeedback);

        form.add(new AjaxButton("submit", form) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                success("Cliente criado com sucesso!");
                target.add(form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(form);
            }
        });
        
        add(form);
    }

    private Label createFeedbackLabel(String id, TextField<?> field) {
        Label label = new Label(id, new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;
            @Override
            public String getObject() {
                var msg = field.getFeedbackMessages().first();
                return msg != null ? msg.getMessage().toString() : "";
            }
        });
        label.setOutputMarkupId(true);
        return label;
    }

    private void addRealTimeValidation(TextField<String> field, Label feedback) {
        field.setOutputMarkupId(true);
        field.add(new AttributeAppender("class", new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;
            @Override
            public String getObject() {
                return !field.getFeedbackMessages().isEmpty() ? " is-invalid" : "";
            }
        }));
        field.add(new AjaxFormComponentUpdatingBehavior("blur") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(field);
                target.add(feedback);
            }
        });
    }
}
