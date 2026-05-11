package com.desafio.estagio.wicket.components.table;

import com.desafio.estagio.dto.ClienteJuridicoDTO;
import com.desafio.estagio.service.ClienteJuridicoService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

public class ClientesJuridicosPanel extends ClienteTablePanel<ClienteJuridicoDTO.Response> {

    @SpringBean
    private ClienteJuridicoService clienteJuridicoService;

    public ClientesJuridicosPanel(String id) {
        super(id);

        add(new Link<Void>("novoClienteBtn") {
            @Override
            public void onClick() {
                System.out.println("Novo cliente jurídico");
            }
        });
    }

    @Override
    protected List<ClienteJuridicoDTO.Response> loadClientes() {
        return clienteJuridicoService.findAll();
    }

    @Override
    protected void populateRow(Item<ClienteJuridicoDTO.Response> item, ClienteJuridicoDTO.Response cliente) {
        item.add(new Label("id", cliente.id()));
        item.add(new Label("razaoSocial", cliente.razaoSocial()));
        item.add(new Label("cnpj", cliente.cnpj()));
        item.add(new Label("inscricaoEstadual", cliente.inscricaoEstadual() != null ? cliente.inscricaoEstadual() : "-"));
        item.add(new Label("email", cliente.email()));

        String statusText = cliente.estaAtivo() ? "Ativo" : "Inativo";
        String statusClass = cliente.estaAtivo() ? "badge bg-success" : "badge bg-danger";
        Label status = new Label("status", statusText);
        status.add(AttributeModifier.replace("class", statusClass));
        item.add(status);

        // Action buttons
        item.add(new Link<Void>("editLink") {
            @Override
            public void onClick() {
                // Edit logic
                System.out.println("Edit cliente: " + cliente.id());
            }
        });

        item.add(new Link<Void>("deleteLink") {
            @Override
            public void onClick() {
                // Delete logic
                System.out.println("Delete cliente: " + cliente.id());
            }
        });
    }
}