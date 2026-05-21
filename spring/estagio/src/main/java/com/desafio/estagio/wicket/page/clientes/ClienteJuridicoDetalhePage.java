package com.desafio.estagio.wicket.page.clientes;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoResponse;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.wicket.component.shared.EnderecoListViewPanel;
import com.desafio.estagio.wicket.page.base.BasePage;
import com.desafio.estagio.wicket.page.home.HomePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serial;

public class ClienteJuridicoDetalhePage extends BasePage {

    @Serial
    private static final long serialVersionUID = 1L;
    private final Long clienteId;
    @SpringBean
    private ClienteJuridicoService clienteJuridicoService;

    public ClienteJuridicoDetalhePage(PageParameters params) {
        super();
        this.clienteId = params.get("clienteId").toLong();

        ClienteJuridicoResponse cliente = clienteJuridicoService.findById(clienteId);

        add(new BookmarkablePageLink<>("voltarBtn", HomePage.class));

        add(new Label("clienteId", cliente.id().toString()));
        add(new Label("clienteCnpj", cliente.cnpj() != null ? cliente.cnpj() : ""));
        add(new Label("clienteRazaoSocial", cliente.razaoSocial() != null ? cliente.razaoSocial() : ""));
        add(new Label("clienteInscricaoEstadual", cliente.inscricaoEstadual() != null ? cliente.inscricaoEstadual() : ""));
        add(new Label("clienteEmail", cliente.email() != null ? cliente.email() : ""));
        add(new Label("clienteDataCriacaoEmpresa", cliente.dataCriacaoEmpresa() != null ? cliente.dataCriacaoEmpresa().toString() : ""));
        add(new Label("clienteStatus", cliente.estaAtivo() != null && cliente.estaAtivo() ? "Ativo" : "Inativo"));

        add(new EnderecoListViewPanel("enderecoPanel", clienteId));
    }
}
