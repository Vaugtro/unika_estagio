package com.desafio.estagio.wicket.page.clientes;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoResponse;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.component.shared.EnderecoListViewPanel;
import com.desafio.estagio.wicket.page.base.BasePage;
import com.desafio.estagio.wicket.page.home.HomePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serial;

public class ClienteDetalhePage extends BasePage {

    @Serial
    private static final long serialVersionUID = 1L;
    private final Long clienteId;
    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    public ClienteDetalhePage(PageParameters params) {
        super();
        this.clienteId = params.get("clienteId").toLong();

        ClienteFisicoResponse cliente = clienteFisicoService.findById(clienteId);

        add(new BookmarkablePageLink<>("voltarBtn", HomePage.class));

        add(new Label("clienteId", cliente.id().toString()));
        add(new Label("clienteNome", cliente.nome() != null ? cliente.nome() : ""));
        add(new Label("clienteCpf", cliente.cpf() != null ? cliente.cpf() : ""));
        add(new Label("clienteRg", cliente.rg() != null ? cliente.rg() : ""));
        add(new Label("clienteEmail", cliente.email() != null ? cliente.email() : ""));
        add(new Label("clienteDataNascimento", cliente.dataNascimento() != null ? cliente.dataNascimento().toString() : ""));
        add(new Label("clienteStatus", cliente.estaAtivo() != null && cliente.estaAtivo() ? "Ativo" : "Inativo"));

        add(new EnderecoListViewPanel("enderecoPanel", clienteId));
    }
}
