package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.WicketTestBase;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClienteFisicoCreateModalTest extends WicketTestBase {

    @Mock
    private ClienteFisicoService clienteFisicoService;

    @BeforeEach
    void setUpBeans() {
        when(applicationContext.getBean(ClienteFisicoService.class)).thenReturn(clienteFisicoService);
    }

    @Test
    void rendersFormFields() {
        tester.startComponentInPage(new ClienteFisicoCreateModal("modal"));

        tester.assertComponent("modal", Panel.class);
        tester.assertComponent("modal:form", Form.class);
        tester.assertComponent("modal:form:cpf", TextField.class);
        tester.assertComponent("modal:form:nome", TextField.class);
        tester.assertComponent("modal:form:rg", TextField.class);
        tester.assertComponent("modal:form:email", TextField.class);
        tester.assertComponent("modal:form:dataNascimento", TextField.class);
        tester.assertComponent("modal:form:submit", org.apache.wicket.ajax.markup.html.form.AjaxButton.class);
        tester.assertNoErrorMessage();
    }

    @Test
    void submitWithInvalidData_doesNotCallService() {
        tester.startComponentInPage(new ClienteFisicoCreateModal("modal"));

        // Submit empty form via Ajax
        tester.executeAjaxEvent("modal:form:submit", "click");

        // Service should never be called when validation fails
        verify(clienteFisicoService, never()).create(any());
    }

    @Test
    void submitWithValidData_callsCreateService() {
        tester.startComponentInPage(new ClienteFisicoCreateModal("modal"));

        // Set form field values as request parameters
        tester.getRequest().addParameter("cpf", "529.982.247-25");
        tester.getRequest().addParameter("nome", "João Silva");
        tester.getRequest().addParameter("rg", "12.345.678-9");
        tester.getRequest().addParameter("email", "joao@test.com");
        tester.getRequest().addParameter("dataNascimento", "15/06/1990");

        // Set endereco fields — the modal constructor creates one endereco item
        tester.getRequest().addParameter("logradouro", "Rua A");
        tester.getRequest().addParameter("numero", "123");
        tester.getRequest().addParameter("bairro", "Centro");
        tester.getRequest().addParameter("cep", "12345-678");
        tester.getRequest().addParameter("cidade", "São Paulo");
        tester.getRequest().addParameter("estado", "SP");
        tester.getRequest().addParameter("telefone", "(11) 99999-9999");

        // Submit via Ajax
        tester.executeAjaxEvent("modal:form:submit", "click");

        // Service should have been called
        verify(clienteFisicoService, atLeastOnce()).create(any());
    }
}
