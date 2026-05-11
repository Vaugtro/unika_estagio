package com.desafio.estagio.wicket.components.form;

import com.desafio.estagio.factory.ClienteFactory;
import com.desafio.estagio.factory.EnderecoFactory;
import com.desafio.estagio.model.*;
import com.desafio.estagio.model.enums.TipoCliente;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import java.util.Arrays;

public class ClienteFormPanel extends Panel {

    @SpringBean
    private ClienteFactory clienteFactory;

    @SpringBean
    private EnderecoFactory enderecoFactory;

    private Cliente cliente;
    private TipoCliente tipoSelecionado;
    private WebMarkupContainer dynamicFormContainer;
    private FeedbackPanel feedbackPanel;

    // Construtor para novo cliente
    public ClienteFormPanel(String id, TipoCliente tipo) {
        super(id);
        this.cliente = clienteFactory.createCliente(tipo);
        this.tipoSelecionado = tipo;
    }

    // Construtor para edição de cliente existente
    public ClienteFormPanel(String id, Cliente clienteExistente) {
        super(id);
        this.cliente = clienteFactory.cloneCliente(clienteExistente);
        this.tipoSelecionado = clienteExistente.getTipo();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Feedback panel para mensagens
        feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        // Formulário principal
        Form<Cliente> form = new Form<>("form", new CompoundPropertyModel<>(cliente));
        add(form);

        // Tipo de cliente
        DropDownChoice<TipoCliente> tipoCliente = new DropDownChoice<>("tipoCliente",
                new PropertyModel<>(this, "tipoSelecionado"),
                Arrays.asList(TipoCliente.values()));
        tipoCliente.setRequired(true);
        tipoCliente.setOutputMarkupId(true);
        tipoCliente.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Recria o cliente com o novo tipo
                cliente = clienteFactory.createCliente(tipoSelecionado);
                updateDynamicForm(target);
                target.add(form);
            }
        });
        form.add(tipoCliente);

        // Email (comum para ambos)
        TextField<String> email = new TextField<>("email");
        email.setRequired(true);
        email.add(EmailAddressValidator.getInstance());
        form.add(email);

        // Status ativo (padrão true)
        DropDownChoice<Boolean> estaAtivo = new DropDownChoice<>("estaAtivo",
                Arrays.asList(true, false));
        estaAtivo.setRequired(true);
        form.add(estaAtivo);

        // Container para formulários específicos (PF ou PJ)
        dynamicFormContainer = new WebMarkupContainer("dynamicForm");
        dynamicFormContainer.setOutputMarkupId(true);
        form.add(dynamicFormContainer);

        // Botão para adicionar novo endereço
        AjaxButton addAddressButton = new AjaxButton("adicionarEndereco") {
            protected void onSubmit(AjaxRequestTarget target) {
                addNewAddress(target);
            }

            protected void onError(AjaxRequestTarget target) {
                target.add(feedbackPanel);
            }
        };
        addAddressButton.setDefaultFormProcessing(false);
        form.add(addAddressButton);

        // Botão de salvar
        AjaxButton saveButton = new AjaxButton("salvar") {
            protected void onSubmit(AjaxRequestTarget target) {
                onSave(target);
            }

            protected void onError(AjaxRequestTarget target) {
                target.add(form);
                target.add(feedbackPanel);
            }
        };
        form.add(saveButton);

        // Botão de limpar
        AjaxButton clearButton = new AjaxButton("limpar") {
            protected void onSubmit(AjaxRequestTarget target) {
                clearForm(target);
            }

            protected void onError(AjaxRequestTarget target) {
                // Não faz nada
            }
        };
        clearButton.setDefaultFormProcessing(false);
        form.add(clearButton);

        // Inicializa o formulário dinâmico
        updateDynamicForm(null);
    }

    private void updateDynamicForm(AjaxRequestTarget target) {
        // Remove todos os componentes do container
        dynamicFormContainer.removeAll();

        if (tipoSelecionado == TipoCliente.FISICA) {
            ClienteFisicoFormPanel formPanel = new ClienteFisicoFormPanel("content", cliente);
            formPanel.setOutputMarkupId(true);
            dynamicFormContainer.add(formPanel);
        } else {
            ClienteJuridicoFormPanel formPanel = new ClienteJuridicoFormPanel("content", cliente);
            formPanel.setOutputMarkupId(true);
            dynamicFormContainer.add(formPanel);
        }

        if (target != null) {
            target.add(dynamicFormContainer);
        }
    }

    private void addNewAddress(AjaxRequestTarget target) {
        try {
            // Adiciona um novo endereço (não será principal)
            cliente.addEndereco(enderecoFactory.createEndereco());

            // Atualiza o formulário dinâmico
            updateDynamicForm(target);

            info("Novo endereço adicionado com sucesso!");
            target.add(feedbackPanel);

        } catch (Exception e) {
            error("Erro ao adicionar endereço: " + e.getMessage());
            target.add(feedbackPanel);
        }
    }

    private void onSave(AjaxRequestTarget target) {
        try {
            // Validações básicas
            if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
                error("Email é obrigatório");
                target.add(feedbackPanel);
                return;
            }

            // Validações específicas por tipo de cliente
            if (tipoSelecionado == TipoCliente.FISICA) {
                validatePessoaFisica();
            } else {
                validatePessoaJuridica();
            }

            // Valida se tem pelo menos um endereço
            if (cliente.getEnderecos() == null || cliente.getEnderecos().isEmpty()) {
                error("Cliente deve ter pelo menos um endereço");
                target.add(feedbackPanel);
                return;
            }

            // Valida se tem exatamente um endereço principal
            long principalCount = cliente.getEnderecos().stream()
                    .filter(Endereco::isPrincipal)
                    .count();

            if (principalCount == 0) {
                error("Cliente deve ter um endereço principal definido");
                target.add(feedbackPanel);
                return;
            }

            if (principalCount > 1) {
                error("Cliente pode ter apenas um endereço principal");
                target.add(feedbackPanel);
                return;
            }

            // TODO: Salvar no banco de dados
            // clienteService.salvar(cliente);

            success("Cliente criado com sucesso!");
            target.add(feedbackPanel);

            // Limpa o formulário para novo cadastro
            clearForm(target);

        } catch (ClassCastException e) {
            error("Erro de tipo de cliente. Por favor, recarregue a página.");
            target.add(feedbackPanel);
        } catch (Exception e) {
            error("Erro ao salvar cliente: " + e.getMessage());
            target.add(feedbackPanel);
        }
    }

    private void validatePessoaFisica() {
        try {
            ClienteFisico clienteFisico = (ClienteFisico) cliente;

            if (clienteFisico.getCpf() == null || clienteFisico.getCpf().trim().isEmpty()) {
                error("CPF é obrigatório para pessoa física");
            }

            if (clienteFisico.getNome() == null || clienteFisico.getNome().trim().isEmpty()) {
                error("Nome é obrigatório para pessoa física");
            }

            if (clienteFisico.getRg() == null || clienteFisico.getRg().trim().isEmpty()) {
                error("RG é obrigatório para pessoa física");
            }

            if (clienteFisico.getDataNascimento() == null) {
                error("Data de nascimento é obrigatória para pessoa física");
            }
        } catch (ClassCastException e) {
            error("Erro: Tipo de cliente inválido para pessoa física");
        }
    }

    private void validatePessoaJuridica() {
        try {
            ClienteJuridico clienteJuridico = (ClienteJuridico) cliente;

            if (clienteJuridico.getCnpj() == null || clienteJuridico.getCnpj().trim().isEmpty()) {
                error("CNPJ é obrigatório para pessoa jurídica");
            }

            if (clienteJuridico.getRazaoSocial() == null || clienteJuridico.getRazaoSocial().trim().isEmpty()) {
                error("Razão social é obrigatória para pessoa jurídica");
            }
        } catch (ClassCastException e) {
            error("Erro: Tipo de cliente inválido para pessoa jurídica");
        }
    }

    private void clearForm(AjaxRequestTarget target) {
        // Cria um novo cliente com um endereço inicial
        this.cliente = clienteFactory.createCliente(TipoCliente.FISICA);
        this.tipoSelecionado = TipoCliente.FISICA;

        // Recria o formulário
        updateDynamicForm(target);
        target.add(getPage());

        info("Formulário limpo para novo cadastro");
        target.add(feedbackPanel);
    }
}