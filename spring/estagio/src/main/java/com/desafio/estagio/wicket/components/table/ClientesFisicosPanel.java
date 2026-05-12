package com.desafio.estagio.wicket.components.table;

import com.desafio.estagio.dto.ClienteFisicoDTO;
import com.desafio.estagio.model.ClienteFisico;
import com.desafio.estagio.model.enums.TipoCliente;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.components.form.ClienteFormPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Iterator;

public class ClientesFisicosPanel extends Panel {

    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    private WebMarkupContainer tableContainer;
    private ModalWindow modal;
    private DataView<ClienteFisicoDTO.ListResponse> dataView;

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
        Form<Void> novoClienteForm = new Form<>("novoClienteBtnForm");
        novoClienteForm.setOutputMarkupId(true);
        add(novoClienteForm);

        AjaxButton novoClienteBtn = new AjaxButton("novoClienteBtn") {
            protected void onSubmit(AjaxRequestTarget target) {
                showClienteForm(target, null);
            }

            protected void onError(AjaxRequestTarget target) {
                // Não faz nada
            }
        };
        novoClienteBtn.setDefaultFormProcessing(false);
        novoClienteForm.add(novoClienteBtn);

        // Container da tabela
        tableContainer = new WebMarkupContainer("tableContainer");
        tableContainer.setOutputMarkupId(true);
        add(tableContainer);

        // Data Provider para clientes físicos
        IDataProvider<ClienteFisicoDTO.ListResponse> dataProvider = new IDataProvider<ClienteFisicoDTO.ListResponse>() {
            @Override
            public Iterator<? extends ClienteFisicoDTO.ListResponse> iterator(long first, long count) {
                Pageable pageable = PageRequest.of((int) (first / count), (int) count);
                Page<ClienteFisicoDTO.ListResponse> page = clienteFisicoService.findAll(pageable);
                return page.getContent().iterator();
            }

            @Override
            public long size() {
                return clienteFisicoService.count();
            }

            @Override
            public IModel<ClienteFisicoDTO.ListResponse> model(ClienteFisicoDTO.ListResponse object) {
                return new CompoundPropertyModel<>(Model.of(object));
            }

            @Override
            public void detach() {
                // Não faz nada
            }
        };

        // DataView para exibir os clientes
        dataView = new DataView<ClienteFisicoDTO.ListResponse>("rows", dataProvider, 10) {
            @Override
            protected void populateItem(Item<ClienteFisicoDTO.ListResponse> item) {
                ClienteFisicoDTO.ListResponse cliente = item.getModelObject();

                item.add(new Label("id", String.valueOf(cliente.id())));
                item.add(new Label("nome", cliente.nome()));
                item.add(new Label("cpf", cliente.cpf()));
                item.add(new Label("email", cliente.email()));

                // Status com badge
                Label status = new Label("status", cliente.estaAtivo() ? "Ativo" : "Inativo");
                status.add(new AttributeModifier("class", cliente.estaAtivo() ? "badge bg-success" : "badge bg-danger"));
                item.add(status);

                // Botão de edição - Add form wrapper
                Form<Void> editarForm = new Form<>("editarBtnForm");
                editarForm.setOutputMarkupId(true);

                AjaxButton editarBtn = new AjaxButton("editarBtn") {
                    protected void onSubmit(AjaxRequestTarget target) {
                        ClienteFisicoDTO.Response clienteCompleto = clienteFisicoService.findById(cliente.id());
                        showClienteForm(target, clienteCompleto);
                    }

                    protected void onError(AjaxRequestTarget target) {
                        // Não faz nada
                    }
                };
                editarBtn.setDefaultFormProcessing(false);
                editarForm.add(editarBtn);
                item.add(editarForm);
            }
        };

        tableContainer.add(dataView);

        // Paginador
        PagingNavigator navigator = new PagingNavigator("navigator", dataView);
        add(navigator);
    }

    private void showClienteForm(AjaxRequestTarget target, ClienteFisicoDTO.Response cliente) {
        ClienteFormPanel clienteFormPanel;

        if (cliente == null) {
            clienteFormPanel = new ClienteFormPanel("content", TipoCliente.FISICA);
        } else {
            ClienteFisico clienteEntity = clienteFisicoService.findEntityById(cliente.id());
            clienteFormPanel = new ClienteFormPanel("content", clienteEntity);
        }

        clienteFormPanel.setOutputMarkupId(true);
        modal.setContent(clienteFormPanel);
        modal.show(target);

        modal.setCloseButtonCallback(ajaxTarget -> {
            ajaxTarget.add(tableContainer);
            return true;
        });
    }
}