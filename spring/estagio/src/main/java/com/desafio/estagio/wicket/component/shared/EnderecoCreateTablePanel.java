package com.desafio.estagio.wicket.component.shared;

import com.desafio.estagio.validation.ValidationConstants;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;

import java.io.Serial;
import java.util.List;

public class EnderecoCreateTablePanel extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final ValidationStyleBehavior VALIDATION_STYLE_INSTANCE = new ValidationStyleBehavior();
    private final ListView<EnderecoCreateFormModel> enderecosView;
    private final List<EnderecoCreateFormModel> enderecos;

    public EnderecoCreateTablePanel(String id, List<EnderecoCreateFormModel> enderecos) {
        super(id);
        this.enderecos = enderecos;
        setOutputMarkupId(true);

        this.enderecosView = new ListView<>("enderecosRow", enderecos) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<EnderecoCreateFormModel> item) {
                item.setModel(new CompoundPropertyModel<>(item.getModelObject()));

                TextField<String> logradouroField = new TextField<>("logradouro", String.class);
                logradouroField.setRequired(true);
                logradouroField.add(StringValidator.lengthBetween(ValidationConstants.LOGRADOURO_MIN, ValidationConstants.LOGRADOURO_MAX));
                logradouroField.add(new AttributeModifier("placeholder", "Logradouro"));
                logradouroField.add(new AttributeModifier("data-field", "logradouro"));
                Label logradouroFeedback = ValidationFeedback.createFeedbackLabel("logradouroFeedback", logradouroField);
                ValidationFeedback.attachRealTimeValidation(logradouroField, logradouroFeedback);
                logradouroField.add(VALIDATION_STYLE_INSTANCE);
                item.add(logradouroField);
                item.add(logradouroFeedback);

                TextField<Long> numeroField = new TextField<>("numero", Long.class);
                numeroField.setRequired(true);
                numeroField.add(new AttributeModifier("placeholder", "Nº"));
                Label numeroFeedback = ValidationFeedback.createFeedbackLabel("numeroFeedback", numeroField);
                ValidationFeedback.attachRealTimeValidation(numeroField, numeroFeedback);
                numeroField.add(VALIDATION_STYLE_INSTANCE);
                item.add(numeroField);
                item.add(numeroFeedback);

                TextField<String> bairroField = new TextField<>("bairro", String.class);
                bairroField.setRequired(true);
                bairroField.add(StringValidator.lengthBetween(ValidationConstants.BAIRRO_MIN, ValidationConstants.BAIRRO_MAX));
                bairroField.add(new AttributeModifier("placeholder", "Bairro"));
                bairroField.add(new AttributeModifier("data-field", "bairro"));
                Label bairroFeedback = ValidationFeedback.createFeedbackLabel("bairroFeedback", bairroField);
                ValidationFeedback.attachRealTimeValidation(bairroField, bairroFeedback);
                bairroField.add(VALIDATION_STYLE_INSTANCE);
                item.add(bairroField);
                item.add(bairroFeedback);

                TextField<String> cepField = new TextField<>("cep", String.class);
                cepField.setRequired(true);
                cepField.add(new PatternValidator("^\\d{5}-?\\d{3}$"));
                cepField.add(StringValidator.maximumLength(ValidationConstants.CEP_MAX));
                cepField.add(new AttributeModifier("placeholder", "CEP"));
                cepField.add(new AttributeModifier("data-mask", "00000-000"));
                cepField.add(new AttributeModifier("onblur", "pesquisacep(this)"));
                cepField.add(new AttributeModifier("data-field", "cep"));
                Label cepFeedback = ValidationFeedback.createFeedbackLabel("cepFeedback", cepField);
                ValidationFeedback.attachRealTimeValidation(cepField, cepFeedback);
                cepField.add(VALIDATION_STYLE_INSTANCE);
                item.add(cepField);
                item.add(cepFeedback);

                TextField<String> cidadeField = new TextField<>("cidade", String.class);
                cidadeField.setRequired(true);
                cidadeField.add(StringValidator.lengthBetween(ValidationConstants.CIDADE_MIN, ValidationConstants.CIDADE_MAX));
                cidadeField.add(new AttributeModifier("placeholder", "Cidade"));
                cidadeField.add(new AttributeModifier("data-field", "cidade"));
                Label cidadeFeedback = ValidationFeedback.createFeedbackLabel("cidadeFeedback", cidadeField);
                ValidationFeedback.attachRealTimeValidation(cidadeField, cidadeFeedback);
                cidadeField.add(VALIDATION_STYLE_INSTANCE);
                item.add(cidadeField);
                item.add(cidadeFeedback);

                TextField<String> estadoField = new TextField<>("estado", String.class);
                estadoField.setRequired(true);
                estadoField.add(StringValidator.exactLength(ValidationConstants.ESTADO_LENGTH));
                estadoField.add(new AttributeModifier("placeholder", "UF"));
                estadoField.add(new AttributeModifier("data-field", "estado"));
                Label estadoFeedback = ValidationFeedback.createFeedbackLabel("estadoFeedback", estadoField);
                ValidationFeedback.attachRealTimeValidation(estadoField, estadoFeedback);
                estadoField.add(VALIDATION_STYLE_INSTANCE);
                item.add(estadoField);
                item.add(estadoFeedback);

                TextField<String> telefoneField = new TextField<>("telefone", String.class);
                telefoneField.setRequired(false);
                telefoneField.add(StringValidator.maximumLength(ValidationConstants.TELEFONE_MAX));
                telefoneField.add(new AttributeModifier("placeholder", "Telefone"));
                telefoneField.add(new AttributeModifier("data-mask", "(00) 00000-0000"));
                telefoneField.add(new PatternValidator("^\\(\\d{2}\\)\\s?\\d{4,5}-?\\d{4}$"));
                Label telefoneFeedback = ValidationFeedback.createFeedbackLabel("telefoneFeedback", telefoneField);
                ValidationFeedback.attachRealTimeValidation(telefoneField, telefoneFeedback);
                telefoneField.add(VALIDATION_STYLE_INSTANCE);
                item.add(telefoneField);
                item.add(telefoneFeedback);

                TextField<String> complementoField = new TextField<>("complemento", String.class);
                complementoField.add(StringValidator.maximumLength(ValidationConstants.COMPLEMENTO_MAX));
                complementoField.add(new AttributeModifier("placeholder", "Complemento"));
                Label complementoFeedback = ValidationFeedback.createFeedbackLabel("complementoFeedback", complementoField);
                ValidationFeedback.attachRealTimeValidation(complementoField, complementoFeedback);
                complementoField.add(VALIDATION_STYLE_INSTANCE);
                item.add(complementoField);
                item.add(complementoFeedback);

                CheckBox principalField = new CheckBox("principal");
                item.add(principalField);

                AjaxLink<Void> removeBtn = new AjaxLink<>("removeBtn") {
                    @Serial
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        if (enderecos.size() > 1) {
                            enderecos.remove(item.getIndex());
                            target.add(EnderecoCreateTablePanel.this);
                            target.appendJavaScript("lucide.createIcons();");
                        }
                    }
                };
                item.add(removeBtn);
            }
        };
        add(enderecosView);

        AjaxLink<Void> addEnderecoBtn = new AjaxLink<>("addEndereco") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                enderecos.add(new EnderecoCreateFormModel());
                target.add(EnderecoCreateTablePanel.this);
                target.appendJavaScript("lucide.createIcons();");
            }
        };
        add(addEnderecoBtn);
    }

    /**
     * Behavior that adds {@code is-invalid} CSS class during render
     * when the component has feedback messages (validation errors).
     */
    private static final class ValidationStyleBehavior extends Behavior {
        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void onComponentTag(Component component, ComponentTag tag) {
            if (!component.getFeedbackMessages().isEmpty()) {
                String cls = tag.getAttribute("class");
                tag.put("class", cls != null ? cls + " is-invalid" : "is-invalid");
            }
        }
    }
}
