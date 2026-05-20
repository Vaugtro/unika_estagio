package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.wicket.model.ClienteJuridicoCreateFormModel;
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
        Label cnpjFeedback = createFeedbackLabel("cnpjFeedback", cnpjField);
        addRealTimeValidation(cnpjField, cnpjFeedback);
        form.add(cnpjField);
        form.add(cnpjFeedback);

        TextField<String> razaoSocialField = new TextField<>("razaoSocial", String.class);
        razaoSocialField.setRequired(true);
        razaoSocialField.add(StringValidator.lengthBetween(3, 150));
        razaoSocialField.add(new AttributeModifier("placeholder", "Razão Social"));
        Label razaoSocialFeedback = createFeedbackLabel("razaoSocialFeedback", razaoSocialField);
        addRealTimeValidation(razaoSocialField, razaoSocialFeedback);
        form.add(razaoSocialField);
        form.add(razaoSocialFeedback);

        TextField<String> ieField = new TextField<>("inscricaoEstadual", String.class);
        ieField.setRequired(true);
        ieField.add(new AttributeModifier("placeholder", "Inscrição Estadual"));
        Label ieFeedback = createFeedbackLabel("ieFeedback", ieField);
        addRealTimeValidation(ieField, ieFeedback);
        form.add(ieField);
        form.add(ieFeedback);

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
