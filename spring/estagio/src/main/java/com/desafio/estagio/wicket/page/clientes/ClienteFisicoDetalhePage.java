package com.desafio.estagio.wicket.page.clientes;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoResponse;
import com.desafio.estagio.model.formatter.CPFFormatter;
import com.desafio.estagio.model.formatter.RGFormatter;
import com.desafio.estagio.service.ClienteFisicoService;
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

public class ClienteFisicoDetalhePage extends BasePage {

    @Serial
    private static final long serialVersionUID = 1L;
    private final Long clienteId;
    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    public ClienteFisicoDetalhePage(PageParameters params) {
        super();
        this.clienteId = params.get("clienteId").toLong();

        ClienteFisicoResponse cliente = clienteFisicoService.findById(clienteId);

        add(new BookmarkablePageLink<>("voltarBtn", HomePage.class));

        add(ComponentAttributeBuilder.of(new Label("clienteId", cliente.id().toString())).setOutputMarkupId(true).build());
        add(ComponentAttributeBuilder.of(new Label("clienteNome", cliente.nome() != null ? cliente.nome() : "")).setOutputMarkupId(true).build());
        add(ComponentAttributeBuilder.of(new Label("clienteCpf", cliente.cpf() != null ? CPFFormatter.format(cliente.cpf()) : "")).setOutputMarkupId(true).build());
        add(ComponentAttributeBuilder.of(new Label("clienteRg", cliente.rg() != null ? RGFormatter.format(cliente.rg()) : "")).setOutputMarkupId(true).build());
        add(ComponentAttributeBuilder.of(new Label("clienteEmail", cliente.email() != null ? cliente.email() : "")).setOutputMarkupId(true).build());
        add(ComponentAttributeBuilder.of(new Label("clienteDataNascimento", cliente.dataNascimento() != null ? cliente.dataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "")).setOutputMarkupId(true).build());
        add(ComponentAttributeBuilder.of(new Label("clienteStatus", cliente.estaAtivo() != null && cliente.estaAtivo() ? "Ativo" : "Inativo")).setOutputMarkupId(true).build());

        AjaxLink<Void> excluirBtn = new AjaxLink<>("excluirBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                ErrorHandler.handleServiceCall(() -> {
                    clienteFisicoService.hardDelete(clienteId);
                    setResponsePage(HomePage.class);
                }, target);
            }
        };
        add(ComponentAttributeBuilder.of(excluirBtn)
                .setVisible(Boolean.FALSE.equals(cliente.estaAtivo()))
                .build());

        add(new EnderecoListViewPanel("enderecoPanel", clienteId));
    }
}
