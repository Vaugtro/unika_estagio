package com.desafio.estagio.wicket.components.table;

import com.desafio.estagio.model.ClienteFisico;
import com.desafio.estagio.model.ClienteFisicoEntity;
import com.desafio.estagio.model.enums.TipoCliente;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.components.form.ClienteFormPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Iterator;

public class ClientesFisicosPanel extends Panel {

    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    private WebMarkupContainer tableContainer;
    private ModalWindow modal;
    private DataView<ClienteFisicoEntity> dataView;

    public ClientesFisicosPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Modal Window para formulário
        modal = new ModalWindow("modal");
        modal.setOutputMarkupId(true);
        modal.setTitle("Cadastro de Cliente");
        modal.setInitialWidth(900);
        modal.setInitialHeight(700);
        modal.setResizable(true);
        add(modal);

        // Botão Novo Cliente
        AjaxButton novoClienteBtn = new AjaxButton("novoClienteBtn") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                showClienteForm(target, null);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                // Não faz nada
            }
        };
        novoClienteBtn.setDefaultFormProcessing(false);
        add(novoClienteBtn);

        // Container da tabela
        tableContainer = new WebMarkupContainer("tableContainer");
        tableContainer.setOutputMarkupId(true);
        add(tableContainer);

        // Data Provider para clientes físicos
        IDataProvider<ClienteFisicoEntity> dataProvider = new IDataProvider<ClienteFisicoEntity>() {
            @Override
            public Iterator<? extends ClienteFisicoEntity> iterator(long first, long count) {
                return clienteFisicoService.findAllPaginated((int) first, (int) count).iterator();
            }

            @Override
            public long size() {
                return clienteFisicoService.count();
            }

            @Override
            public IModel<ClienteFisicoEntity> model(ClienteFisicoEntity object) {
                return new CompoundPropertyModel<>(Model.of(object));
            }

            @Override
            public void detach() {
                // Não faz nada
            }
        };

        // DataView para exibir os clientes
        dataView = new DataView<ClienteFisicoEntity>("rows", dataProvider, 10) {
            @Override
            protected void populateItem(Item<ClienteFisicoEntity> item) {
                ClienteFisicoEntity cliente = item.getModelObject();

                item.add(new Label("id", cliente.getId()));
                item.add(new Label("nome", cliente.getNome()));
                item.add(new Label("cpf", cliente.getCpf()));
                item.add(new Label("rg", cliente.getRg()));
                item.add(new Label("email", cliente.getEmail()));

                // Status com badge
                Label status = new Label("status", cliente.getEstaAtivo() ? "Ativo" : "Inativo");
                status.add(new AttributeModifier("class", cliente.getEstaAtivo() ? "badge bg-success" : "badge bg-danger"));
                item.add(status);

                // Botão de edição
                AjaxButton editarBtn = new AjaxButton("editarBtn") {
                    protected void onSubmit(AjaxRequestTarget target) {
                        showClienteForm(target, cliente);
                    }

                    protected void onError(AjaxRequestTarget target) {
                        // Não faz nada
                    }
                };
                editarBtn.setDefaultFormProcessing(false);
                item.add(editarBtn);
            }
        };

        tableContainer.add(dataView);

        // Paginador
        PagingNavigator navigator = new PagingNavigator("navigator", dataView);
        add(navigator);
    }

    private void showClienteForm(AjaxRequestTarget target, ClienteFisicoEntity cliente) {
        // Cria o formulário
        ClienteFormPanel clienteFormPanel;

        if (cliente == null) {
            // Novo cliente - passa apenas o tipo
            clienteFormPanel = new ClienteFormPanel("content", TipoCliente.FISICA);
        } else {
            // Edição - passa o cliente existente
            clienteFormPanel = new ClienteFormPanel("content", cliente);
        }

        clienteFormPanel.setOutputMarkupId(true);

        // Configura o modal
        modal.setContent(clienteFormPanel);
        modal.show(target);

        // Listener para quando o modal fechar, recarregar a tabela
        modal.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget target) {
                target.add(tableContainer);
                return true;
            }
        });
    }
}