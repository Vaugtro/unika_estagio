package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.wicket.WicketTestBase;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClienteJuridicoCreateModalTest extends WicketTestBase {

    @Mock
    private ClienteJuridicoService clienteJuridicoService;

    @BeforeEach
    void setUpBeans() {
        when(applicationContext.getBean(ClienteJuridicoService.class)).thenReturn(clienteJuridicoService);
    }

    @Test
    void rendersFormFields() {
        tester.startComponentInPage(new ClienteJuridicoCreateModal("modal"));

        tester.assertComponent("modal", Panel.class);
        tester.assertComponent("modal:form", Form.class);
        tester.assertComponent("modal:form:cnpj", TextField.class);
        tester.assertComponent("modal:form:razaoSocial", TextField.class);
        tester.assertComponent("modal:form:inscricaoEstadual", TextField.class);
        tester.assertComponent("modal:form:email", TextField.class);
        tester.assertComponent("modal:form:dataCriacaoEmpresa", TextField.class);
        tester.assertComponent("modal:form:submit", org.apache.wicket.ajax.markup.html.form.AjaxButton.class);
        tester.assertNoErrorMessage();
    }

    @Test
    void submitWithInvalidData_doesNotCallService() {
        tester.startComponentInPage(new ClienteJuridicoCreateModal("modal"));

        // Submit empty form via Ajax
        tester.executeAjaxEvent("modal:form:submit", "click");

        // Service should never be called when validation fails
        verify(clienteJuridicoService, never()).create(any());
    }

    @Test
    void submitWithValidData_callsCreateService() {
        tester.startComponentInPage(new ClienteJuridicoCreateModal("modal"));

        // Set form field values as request parameters
        tester.getRequest().addParameter("cnpj", "11.444.777/0001-61");
        tester.getRequest().addParameter("razaoSocial", "Empresa Ltda");
        tester.getRequest().addParameter("inscricaoEstadual", "123456789");
        tester.getRequest().addParameter("email", "contato@empresa.com");
        tester.getRequest().addParameter("dataCriacaoEmpresa", "01/01/2020");

        // Set endereco fields — the modal constructor creates one endereco item
        tester.getRequest().addParameter("logradouro", "Rua B");
        tester.getRequest().addParameter("numero", "456");
        tester.getRequest().addParameter("bairro", "Jardim");
        tester.getRequest().addParameter("cep", "98765-432");
        tester.getRequest().addParameter("cidade", "Rio de Janeiro");
        tester.getRequest().addParameter("estado", "RJ");
        tester.getRequest().addParameter("telefone", "(21) 88888-8888");

        // Submit via Ajax
        tester.executeAjaxEvent("modal:form:submit", "click");

        // Service should have been called
        verify(clienteJuridicoService, atLeastOnce()).create(any());
    }
}
