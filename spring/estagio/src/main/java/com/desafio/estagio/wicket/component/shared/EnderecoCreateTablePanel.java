package com.desafio.estagio.wicket.component.shared;

import com.desafio.estagio.validation.ValidationConstants;
import com.desafio.estagio.wicket.builder.FormFieldBuilder;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;
import com.desafio.estagio.wicket.util.JavaScriptUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;

import wicket.js.WicketJsAnchor;

import java.io.Serial;
import java.util.List;

public class EnderecoCreateTablePanel extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final ValidationStyleBehavior VALIDATION_STYLE_INSTANCE = new ValidationStyleBehavior();
    private static final ResourceReference VIA_CEP_JS = new JavaScriptResourceReference(WicketJsAnchor.class, "viacep.js");
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

                var logradouroBundle = FormFieldBuilder.create(String.class)
                    .id("logradouro")
                    .required()
                    .validator(StringValidator.lengthBetween(ValidationConstants.LOGRADOURO_MIN, ValidationConstants.LOGRADOURO_MAX))
                    .placeholder("Logradouro")
                    .attribute("data-field", "logradouro")
                    .feedbackLabel("logradouroFeedback")
                    .realTimeValidation()
                    .build();
                logradouroBundle.field().add(VALIDATION_STYLE_INSTANCE);
                item.add(logradouroBundle.field());
                item.add(logradouroBundle.feedbackLabel());

                var numeroBundle = FormFieldBuilder.create(Long.class)
                    .id("numero")
                    .required()
                    .placeholder("Nº")
                    .feedbackLabel("numeroFeedback")
                    .realTimeValidation()
                    .build();
                numeroBundle.field().add(VALIDATION_STYLE_INSTANCE);
                item.add(numeroBundle.field());
                item.add(numeroBundle.feedbackLabel());

                var bairroBundle = FormFieldBuilder.create(String.class)
                    .id("bairro")
                    .required()
                    .validator(StringValidator.lengthBetween(ValidationConstants.BAIRRO_MIN, ValidationConstants.BAIRRO_MAX))
                    .placeholder("Bairro")
                    .attribute("data-field", "bairro")
                    .feedbackLabel("bairroFeedback")
                    .realTimeValidation()
                    .build();
                bairroBundle.field().add(VALIDATION_STYLE_INSTANCE);
                item.add(bairroBundle.field());
                item.add(bairroBundle.feedbackLabel());

                var cepBundle = FormFieldBuilder.create(String.class)
                    .id("cep")
                    .required()
                    .validator(new PatternValidator("^\\d{5}-?\\d{3}$"))
                    .validator(StringValidator.maximumLength(ValidationConstants.CEP_MAX))
                    .placeholder("CEP")
                    .attribute("data-mask", "00000-000")
                    .attribute("onblur", "pesquisacep(this)")
                    .attribute("data-field", "cep")
                    .feedbackLabel("cepFeedback")
                    .realTimeValidation()
                    .build();
                cepBundle.field().add(VALIDATION_STYLE_INSTANCE);
                item.add(cepBundle.field());
                item.add(cepBundle.feedbackLabel());

                var cidadeBundle = FormFieldBuilder.create(String.class)
                    .id("cidade")
                    .required()
                    .validator(StringValidator.lengthBetween(ValidationConstants.CIDADE_MIN, ValidationConstants.CIDADE_MAX))
                    .placeholder("Cidade")
                    .attribute("data-field", "cidade")
                    .feedbackLabel("cidadeFeedback")
                    .realTimeValidation()
                    .build();
                cidadeBundle.field().add(VALIDATION_STYLE_INSTANCE);
                item.add(cidadeBundle.field());
                item.add(cidadeBundle.feedbackLabel());

                var estadoBundle = FormFieldBuilder.create(String.class)
                    .id("estado")
                    .required()
                    .validator(StringValidator.exactLength(ValidationConstants.ESTADO_LENGTH))
                    .placeholder("UF")
                    .attribute("data-field", "estado")
                    .feedbackLabel("estadoFeedback")
                    .realTimeValidation()
                    .build();
                estadoBundle.field().add(VALIDATION_STYLE_INSTANCE);
                item.add(estadoBundle.field());
                item.add(estadoBundle.feedbackLabel());

                var telefoneBundle = FormFieldBuilder.create(String.class)
                    .id("telefone")
                    .validator(StringValidator.maximumLength(ValidationConstants.TELEFONE_MAX))
                    .placeholder("Telefone")
                    .attribute("data-mask", "(00) 00000-0000")
                    .validator(new PatternValidator("^\\(\\d{2}\\)\\s?\\d{4,5}-?\\d{4}$"))
                    .feedbackLabel("telefoneFeedback")
                    .realTimeValidation()
                    .build();
                telefoneBundle.field().add(VALIDATION_STYLE_INSTANCE);
                item.add(telefoneBundle.field());
                item.add(telefoneBundle.feedbackLabel());

                var complementoBundle = FormFieldBuilder.create(String.class)
                    .id("complemento")
                    .validator(StringValidator.maximumLength(ValidationConstants.COMPLEMENTO_MAX))
                    .placeholder("Complemento")
                    .feedbackLabel("complementoFeedback")
                    .realTimeValidation()
                    .build();
                complementoBundle.field().add(VALIDATION_STYLE_INSTANCE);
                item.add(complementoBundle.field());
                item.add(complementoBundle.feedbackLabel());

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
                            JavaScriptUtils.reloadLucideIcons(target);
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
                JavaScriptUtils.reloadLucideIcons(target);
                JavaScriptUtils.reinitializeMasks(target);
            }
        };
        add(addEnderecoBtn);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(VIA_CEP_JS));
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
