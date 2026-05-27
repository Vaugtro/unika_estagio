package com.desafio.estagio.wicket.page.clientes;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoResponse;
import com.desafio.estagio.model.formatter.CNPJFormatter;
import com.desafio.estagio.wicket.builder.ComponentAttributeBuilder;
import com.desafio.estagio.wicket.util.ErrorHandler;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.wicket.component.shared.EnderecoListViewPanel;
import com.desafio.estagio.wicket.page.base.BasePage;
import com.desafio.estagio.wicket.page.home.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serial;
import java.time.format.DateTimeFormatter;

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
        add(new Label("clienteCnpj", cliente.cnpj() != null ? CNPJFormatter.format(cliente.cnpj()) : ""));
        add(new Label("clienteRazaoSocial", cliente.razaoSocial() != null ? cliente.razaoSocial() : ""));
        add(new Label("clienteInscricaoEstadual", cliente.inscricaoEstadual() != null ? cliente.inscricaoEstadual() : ""));
        add(new Label("clienteEmail", cliente.email() != null ? cliente.email() : ""));
        add(new Label("clienteDataCriacaoEmpresa", cliente.dataCriacaoEmpresa() != null ? cliente.dataCriacaoEmpresa().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""));
        add(new Label("clienteStatus", cliente.estaAtivo() != null && cliente.estaAtivo() ? "Ativo" : "Inativo"));

        AjaxLink<Void> excluirBtn = new AjaxLink<>("excluirBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                ErrorHandler.handleDelete(target, () -> clienteJuridicoService.hardDelete(clienteId));
                setResponsePage(HomePage.class);
            }
        };
        ComponentAttributeBuilder.of(excluirBtn)
                .setVisible(Boolean.FALSE.equals(cliente.estaAtivo()))
                .build();
        add(excluirBtn);

        add(new EnderecoListViewPanel("enderecoPanel", clienteId));
    }
}
