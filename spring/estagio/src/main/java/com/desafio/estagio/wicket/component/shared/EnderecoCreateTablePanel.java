package com.desafio.estagio.wicket.component.shared;

import com.desafio.estagio.validation.ValidationConstants;
import com.desafio.estagio.wicket.builder.FormFieldBuilder;
import com.desafio.estagio.wicket.builder.FormFieldBundle;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;
import com.desafio.estagio.wicket.util.JavaScriptUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

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

                FormFieldBundle logradouro = FormFieldBuilder.create(String.class)
                        .id("logradouro").required()
                        .placeholder("Logradouro").dataField("logradouro")
                        .maxLength(ValidationConstants.LOGRADOURO_MAX)
                        .minLength(ValidationConstants.LOGRADOURO_MIN)
                        .feedbackLabel("logradouroFeedback")
                        .realTimeValidation().validationStyle(VALIDATION_STYLE_INSTANCE)
                        .build();
                item.add(logradouro.getField());
                item.add(logradouro.getFeedbackLabel());

                FormFieldBundle numero = FormFieldBuilder.create(Long.class)
                        .id("numero").required()
                        .placeholder("Nº")
                        .feedbackLabel("numeroFeedback")
                        .realTimeValidation().validationStyle(VALIDATION_STYLE_INSTANCE)
                        .build();
                item.add(numero.getField());
                item.add(numero.getFeedbackLabel());

                FormFieldBundle bairro = FormFieldBuilder.create(String.class)
                        .id("bairro").required()
                        .placeholder("Bairro").dataField("bairro")
                        .maxLength(ValidationConstants.BAIRRO_MAX)
                        .minLength(ValidationConstants.BAIRRO_MIN)
                        .feedbackLabel("bairroFeedback")
                        .realTimeValidation().validationStyle(VALIDATION_STYLE_INSTANCE)
                        .build();
                item.add(bairro.getField());
                item.add(bairro.getFeedbackLabel());

                FormFieldBundle cep = FormFieldBuilder.create(String.class)
                        .id("cep").required()
                        .placeholder("CEP").dataField("cep")
                        .dataMask("00000-000").onblur("pesquisacep(this)")
                        .maxLength(ValidationConstants.CEP_MAX)
                        .pattern("^\\d{5}-?\\d{3}$")
                        .feedbackLabel("cepFeedback")
                        .realTimeValidation().validationStyle(VALIDATION_STYLE_INSTANCE)
                        .build();
                item.add(cep.getField());
                item.add(cep.getFeedbackLabel());

                FormFieldBundle cidade = FormFieldBuilder.create(String.class)
                        .id("cidade").required()
                        .placeholder("Cidade").dataField("cidade")
                        .maxLength(ValidationConstants.CIDADE_MAX)
                        .minLength(ValidationConstants.CIDADE_MIN)
                        .feedbackLabel("cidadeFeedback")
                        .realTimeValidation().validationStyle(VALIDATION_STYLE_INSTANCE)
                        .build();
                item.add(cidade.getField());
                item.add(cidade.getFeedbackLabel());

                FormFieldBundle estado = FormFieldBuilder.create(String.class)
                        .id("estado").required()
                        .placeholder("UF").dataField("estado")
                        .exactLength(ValidationConstants.ESTADO_LENGTH)
                        .feedbackLabel("estadoFeedback")
                        .realTimeValidation().validationStyle(VALIDATION_STYLE_INSTANCE)
                        .build();
                item.add(estado.getField());
                item.add(estado.getFeedbackLabel());

                FormFieldBundle telefone = FormFieldBuilder.create(String.class)
                        .id("telefone")
                        .placeholder("Telefone").dataMask("(00) 00000-0000")
                        .maxLength(ValidationConstants.TELEFONE_MAX)
                        .pattern("^\\(\\d{2}\\)\\s?\\d{4,5}-?\\d{4}$")
                        .feedbackLabel("telefoneFeedback")
                        .realTimeValidation().validationStyle(VALIDATION_STYLE_INSTANCE)
                        .build();
                item.add(telefone.getField());
                item.add(telefone.getFeedbackLabel());

                FormFieldBundle complemento = FormFieldBuilder.create(String.class)
                        .id("complemento")
                        .placeholder("Complemento")
                        .maxLength(ValidationConstants.COMPLEMENTO_MAX)
                        .feedbackLabel("complementoFeedback")
                        .realTimeValidation().validationStyle(VALIDATION_STYLE_INSTANCE)
                        .build();
                item.add(complemento.getField());
                item.add(complemento.getFeedbackLabel());

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
                            JavaScriptUtils.createIcons(target);
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
                JavaScriptUtils.createIcons(target);
                JavaScriptUtils.reapplyMasks(target);
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

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(
                new JavaScriptResourceReference(JavaScriptUtils.class, "js/viacep.js")
        ));
    }
}
