package com.desafio.estagio.wicket.components.form;

import com.desafio.estagio.model.Cliente;
import com.desafio.estagio.model.Endereco;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

public class ClienteFisicoFormPanel extends Panel {

    private final Cliente cliente;

    public ClienteFisicoFormPanel(String id, Cliente cliente) {
        super(id, new CompoundPropertyModel<>(cliente));
        this.cliente = cliente;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Nome completo
        TextField<String> nome = new TextField<>("nome");
        nome.setRequired(true);
        add(nome);

        // CPF
        TextField<String> cpf = new TextField<>("cpf");
        cpf.setRequired(true);
        cpf.add(new PatternValidator("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}"));
        add(cpf);

        // RG
        TextField<String> rg = new TextField<>("rg");
        rg.setRequired(true);
        add(rg);

        // Data de Nascimento
        DateTextField dataNascimento = new DateTextField("dataNascimento",
                new StyleDateConverter("dd/MM/yyyy", true));
        dataNascimento.setRequired(true);
        add(dataNascimento);

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
                            target.add(ClienteFisicoFormPanel.this);
                        }
                    }
                };
                item.add(principal);

                // Botão remover (só mostra se tiver mais de um endereço)
                AjaxButton removeButton = new AjaxButton("remover") {
                    protected void onSubmit(AjaxRequestTarget target) {
                        try {
                            cliente.removeEndereco(endereco);
                            target.add(ClienteFisicoFormPanel.this);
                        } catch (IllegalStateException e) {
                            error(e.getMessage());
                            target.add(getPage());
                        }
                    }
                };
                removeButton.setVisible(cliente.getEnderecos().size() > 1);
                item.add(removeButton);
            }
        };

        add(enderecosList);
    }
}