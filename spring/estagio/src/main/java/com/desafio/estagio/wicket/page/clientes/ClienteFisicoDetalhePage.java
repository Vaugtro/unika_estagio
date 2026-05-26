package com.desafio.estagio.wicket.page.clientes;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoResponse;
import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.model.formatter.CPFFormatter;
import com.desafio.estagio.model.formatter.RGFormatter;
import com.desafio.estagio.service.ClienteFisicoService;
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

        add(new Label("clienteId", cliente.id().toString()));
        add(new Label("clienteNome", cliente.nome() != null ? cliente.nome() : ""));
        add(new Label("clienteCpf", cliente.cpf() != null ? CPFFormatter.format(cliente.cpf()) : ""));
        add(new Label("clienteRg", cliente.rg() != null ? RGFormatter.format(cliente.rg()) : ""));
        add(new Label("clienteEmail", cliente.email() != null ? cliente.email() : ""));
        add(new Label("clienteDataNascimento", cliente.dataNascimento() != null ? cliente.dataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""));
        add(new Label("clienteStatus", cliente.estaAtivo() != null && cliente.estaAtivo() ? "Ativo" : "Inativo"));

        AjaxLink<Void> excluirBtn = new AjaxLink<>("excluirBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    clienteFisicoService.hardDelete(clienteId);
                    setResponsePage(HomePage.class);
                } catch (BusinessException e) {
                    // should not happen since button only shows when inactive
                }
            }
        };
        excluirBtn.setVisible(Boolean.FALSE.equals(cliente.estaAtivo()));
        add(excluirBtn);

        add(new EnderecoListViewPanel("enderecoPanel", clienteId));
    }
}
