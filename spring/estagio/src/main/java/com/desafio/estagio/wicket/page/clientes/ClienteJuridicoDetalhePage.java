package com.desafio.estagio.wicket.page.clientes;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoResponse;
import com.desafio.estagio.model.formatter.CNPJFormatter;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.wicket.builder.ComponentAttributeBuilder;
import com.desafio.estagio.wicket.component.shared.EnderecoListViewPanel;
import com.desafio.estagio.wicket.page.base.BasePage;
import com.desafio.estagio.wicket.page.home.HomePage;
import com.desafio.estagio.wicket.util.ErrorHandler;
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

        add(ComponentAttributeBuilder.of(new Label("clienteId", cliente.id().toString())).build());
        add(ComponentAttributeBuilder.of(new Label("clienteCnpj", cliente.cnpj() != null ? CNPJFormatter.format(cliente.cnpj()) : "")).build());
        add(ComponentAttributeBuilder.of(new Label("clienteRazaoSocial", cliente.razaoSocial() != null ? cliente.razaoSocial() : "")).build());
        add(ComponentAttributeBuilder.of(new Label("clienteInscricaoEstadual", cliente.inscricaoEstadual() != null ? cliente.inscricaoEstadual() : "")).build());
        add(ComponentAttributeBuilder.of(new Label("clienteEmail", cliente.email() != null ? cliente.email() : "")).build());
        add(ComponentAttributeBuilder.of(new Label("clienteDataCriacaoEmpresa", cliente.dataCriacaoEmpresa() != null ? cliente.dataCriacaoEmpresa().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "")).build());
        add(ComponentAttributeBuilder.of(new Label("clienteStatus", cliente.estaAtivo() != null && cliente.estaAtivo() ? "Ativo" : "Inativo")).build());

        AjaxLink<Void> excluirBtn = ComponentAttributeBuilder.of(new AjaxLink<Void>("excluirBtn") {
                    @Serial
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        ErrorHandler.handleServiceCall(() -> {
                            clienteJuridicoService.hardDelete(clienteId);
                            setResponsePage(HomePage.class);
                        }, target);
                    }
                })
                .setVisible(Boolean.FALSE.equals(cliente.estaAtivo()))
                .build();
        add(excluirBtn);

        add(new EnderecoListViewPanel("enderecoPanel", clienteId));
    }
}
