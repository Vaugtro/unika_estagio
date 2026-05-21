package com.desafio.estagio.wicket.component.shared;

import com.desafio.estagio.validation.ValidationConstants;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import java.io.Serial;
import java.util.List;

public class EnderecoCreateTablePanel extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;

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
                logradouroField.add(new AttributeModifier("placeholder", "Logradouro"));
                item.add(logradouroField);

                TextField<Long> numeroField = new TextField<>("numero", Long.class);
                numeroField.setRequired(true);
                numeroField.add(new AttributeModifier("placeholder", "Nº"));
                item.add(numeroField);

                TextField<String> bairroField = new TextField<>("bairro", String.class);
                bairroField.setRequired(true);
                bairroField.add(new AttributeModifier("placeholder", "Bairro"));
                item.add(bairroField);

                TextField<String> cepField = new TextField<>("cep", String.class);
                cepField.setRequired(true);
                cepField.add(new AttributeModifier("placeholder", "CEP"));
                cepField.add(new AttributeModifier("data-mask", "00000-000"));
                item.add(cepField);

                TextField<String> cidadeField = new TextField<>("cidade", String.class);
                cidadeField.setRequired(true);
                cidadeField.add(new AttributeModifier("placeholder", "Cidade"));
                item.add(cidadeField);

                TextField<String> estadoField = new TextField<>("estado", String.class);
                estadoField.setRequired(true);
                estadoField.add(StringValidator.exactLength(ValidationConstants.ESTADO_LENGTH));
                estadoField.add(new AttributeModifier("placeholder", "UF"));
                item.add(estadoField);

                TextField<String> telefoneField = new TextField<>("telefone", String.class);
                telefoneField.setRequired(true);
                telefoneField.add(new AttributeModifier("placeholder", "Telefone"));
                telefoneField.add(new AttributeModifier("data-mask", "(00) 00000-0000"));
                item.add(telefoneField);

                TextField<String> complementoField = new TextField<>("complemento", String.class);
                complementoField.add(new AttributeModifier("placeholder", "Complemento"));
                item.add(complementoField);

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
            }
        };
        add(addEnderecoBtn);
    }
}
