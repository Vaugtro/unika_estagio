package com.desafio.estagio.wicket.components.form;

import com.desafio.estagio.model.Cliente;
import com.desafio.estagio.model.Endereco;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.jspecify.annotations.NonNull;

public class ClienteJuridicoFormPanel extends Panel {

    private final Cliente cliente;

    public ClienteJuridicoFormPanel(String id, Cliente cliente) {
        super(id, new CompoundPropertyModel<>(cliente));
        this.cliente = cliente;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Razão Social
        TextField<String> razaoSocial = new TextField<>("razaoSocial");
        razaoSocial.setRequired(true);
        add(razaoSocial);

        // Nome Fantasia
        TextField<String> nomeFantasia = new TextField<>("nomeFantasia");
        nomeFantasia.setRequired(true);
        add(nomeFantasia);

        // CNPJ
        TextField<String> cnpj = new TextField<>("cnpj");
        cnpj.setRequired(true);
        cnpj.add(new PatternValidator("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}"));
        add(cnpj);

        // Inscrição Estadual (opcional)
        TextField<String> inscricaoEstadual = new TextField<>("inscricaoEstadual");
        add(inscricaoEstadual);

        // Lista de endereços
        final ListView<Endereco> enderecosList = new ListView<Endereco>("enderecos", cliente.getEnderecos()) {
            @Override
            protected void populateItem(ListItem<Endereco> item) {
                final Endereco endereco = item.getModelObject();
                final CompoundPropertyModel<Endereco> model = new CompoundPropertyModel<>(endereco);
                item.setModel(model);

                // Número do endereço
                item.add(new Label("indice", String.valueOf(item.getIndex() + 1)));

                // Logradouro
                TextField<String> logradouro = new TextField<>("logradouro");
                logradouro.setRequired(true);
                item.add(logradouro);

                // Número
                TextField<Integer> numero = new TextField<>("numero");
                numero.setRequired(true);
                item.add(numero);

                // Complemento
                TextField<String> complemento = new TextField<>("complemento");
                item.add(complemento);

                // CEP
                TextField<String> cep = new TextField<>("cep");
                cep.setRequired(true);
                cep.add(new PatternValidator("\\d{5}-\\d{3}"));
                item.add(cep);

                // Bairro
                TextField<String> bairro = new TextField<>("bairro");
                bairro.setRequired(true);
                item.add(bairro);

                // Cidade
                TextField<String> cidade = new TextField<>("cidade");
                cidade.setRequired(true);
                item.add(cidade);

                // Estado
                TextField<String> estado = new TextField<>("estado");
                estado.setRequired(true);
                estado.add(new PatternValidator("[A-Z]{2}"));
                item.add(estado);

                // Telefone
                TextField<String> telefone = new TextField<>("telefone");
                telefone.setRequired(true);
                telefone.add(new PatternValidator("\\(\\d{2}\\) \\d{4,5}-\\d{4}"));
                item.add(telefone);

                // Checkbox de endereço principal
                AjaxCheckBox principal = new AjaxCheckBox("principal", new PropertyModel<>(endereco, "principal")) {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        if (getModelObject()) {
                            // Se este for marcado como principal, desmarca todos os outros
                            for (Endereco e : cliente.getEnderecos()) {
                                if (e != endereco) {
                                    e.setPrincipal(false);
                                }
                            }
                            target.add(ClienteJuridicoFormPanel.this);
                        }
                    }
                };
                item.add(principal);

                // Botão remover (só mostra se tiver mais de um endereço)
                AjaxButton removeButton = getComponents(endereco);
                item.add(removeButton);
            }

            private @NonNull AjaxButton getComponents(Endereco endereco) {
                AjaxButton removeButton = new AjaxButton("remover") {
                    private void onSubmit(AjaxRequestTarget target) {
                        try {
                            cliente.removeEndereco(endereco);
                            target.add(ClienteJuridicoFormPanel.this);
                        } catch (IllegalStateException e) {
                            error(e.getMessage());
                            target.add(getPage());
                        }
                    }
                };
                removeButton.setVisible(cliente.getEnderecos().size() > 1);
                return removeButton;
            }
        };

        add(enderecosList);
    }
}