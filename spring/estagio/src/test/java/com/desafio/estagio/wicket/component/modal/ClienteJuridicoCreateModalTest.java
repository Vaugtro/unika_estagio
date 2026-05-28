package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.model.UnidadeFederativa;
import com.desafio.estagio.repository.UnidadeFederativaRepository;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoCreateRequest;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.wicket.WicketTestBase;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClienteJuridicoCreateModalTest extends WicketTestBase {

    private static final String MODAL = "modal";

    @BeforeEach
    void configureMocks() {
        var repo = mock(UnidadeFederativaRepository.class);
        when(repo.findAll()).thenReturn(List.of(
                new UnidadeFederativa("AC", "Acre"), new UnidadeFederativa("AL", "Alagoas"),
                new UnidadeFederativa("AP", "Amapá"), new UnidadeFederativa("AM", "Amazonas"),
                new UnidadeFederativa("BA", "Bahia"), new UnidadeFederativa("CE", "Ceará"),
                new UnidadeFederativa("DF", "Distrito Federal"), new UnidadeFederativa("ES", "Espírito Santo"),
                new UnidadeFederativa("GO", "Goiás"), new UnidadeFederativa("MA", "Maranhão"),
                new UnidadeFederativa("MT", "Mato Grosso"), new UnidadeFederativa("MS", "Mato Grosso do Sul"),
                new UnidadeFederativa("MG", "Minas Gerais"), new UnidadeFederativa("PA", "Pará"),
                new UnidadeFederativa("PB", "Paraíba"), new UnidadeFederativa("PR", "Paraná"),
                new UnidadeFederativa("PE", "Pernambuco"), new UnidadeFederativa("PI", "Piauí"),
                new UnidadeFederativa("RJ", "Rio de Janeiro"), new UnidadeFederativa("RN", "Rio Grande do Norte"),
                new UnidadeFederativa("RS", "Rio Grande do Sul"), new UnidadeFederativa("RO", "Rondônia"),
                new UnidadeFederativa("RR", "Roraima"), new UnidadeFederativa("SC", "Santa Catarina"),
                new UnidadeFederativa("SP", "São Paulo"), new UnidadeFederativa("SE", "Sergipe"),
                new UnidadeFederativa("TO", "Tocantins")
        ));
        configureMock(UnidadeFederativaRepository.class, repo);
    }

    @Test
    void testRender() {
        var modal = new ClienteJuridicoCreateModal(MODAL);
        tester.startComponentInPage(modal);

        tester.assertComponent(MODAL, ClienteJuridicoCreateModal.class);
        tester.assertComponent(MODAL + ":form", Form.class);

        // Form fields
        tester.assertComponent(MODAL + ":form:cnpj", TextField.class);
        tester.assertComponent(MODAL + ":form:razaoSocial", TextField.class);
        tester.assertComponent(MODAL + ":form:inscricaoEstadual", TextField.class);
        tester.assertComponent(MODAL + ":form:email", TextField.class);
        tester.assertComponent(MODAL + ":form:dataCriacaoEmpresa", TextField.class);

        // Feedback labels
        tester.assertComponent(MODAL + ":form:cnpjFeedback", Label.class);
        tester.assertComponent(MODAL + ":form:razaoSocialFeedback", Label.class);
        tester.assertComponent(MODAL + ":form:ieFeedback", Label.class);
        tester.assertComponent(MODAL + ":form:emailFeedback", Label.class);
        tester.assertComponent(MODAL + ":form:dataCriacaoEmpresaFeedback", Label.class);

        // Endereco table panel
        tester.assertComponent(MODAL + ":form:enderecosContainer",
                com.desafio.estagio.wicket.component.shared.EnderecoCreateTablePanel.class);

        // First endereco row fields
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:logradouro", TextField.class);
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:numero", TextField.class);
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:bairro", TextField.class);
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:cep", TextField.class);
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:cidade", TextField.class);
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:estado", DropDownChoice.class);
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:telefone", TextField.class);
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:complemento", TextField.class);

        // Submit button
        tester.assertComponent(MODAL + ":form:submit",
                org.apache.wicket.ajax.markup.html.form.AjaxButton.class);

        tester.assertNoErrorMessage();
    }

    @Test
    void testValidSubmit() throws Exception {
        var modal = new ClienteJuridicoCreateModal(MODAL);
        tester.startComponentInPage(modal);

        FormTester formTester = tester.newFormTester(MODAL + ":form");
        formTester.setValue("cnpj", "11.222.333/0001-81");
        formTester.setValue("razaoSocial", "Empresa Exemplo LTDA");
        formTester.setValue("inscricaoEstadual", "123456789");
        formTester.setValue("email", "contato@empresa.com");
        formTester.setValue("dataCriacaoEmpresa", "15/01/2020");

        // Endereco 0 required fields
        formTester.setValue("enderecosContainer:enderecosRow:0:logradouro", "Av. Paulista");
        formTester.setValue("enderecosContainer:enderecosRow:0:numero", "1000");
        formTester.setValue("enderecosContainer:enderecosRow:0:bairro", "Bela Vista");
        formTester.setValue("enderecosContainer:enderecosRow:0:cep", "01310-100");
        formTester.setValue("enderecosContainer:enderecosRow:0:cidade", "São Paulo");
        formTester.setValue("enderecosContainer:enderecosRow:0:estado", "SP");

        tester.executeAjaxEvent(MODAL + ":form:submit", "click");

        Field serviceField = ClienteJuridicoCreateModal.class.getDeclaredField("clienteJuridicoService");
        serviceField.setAccessible(true);
        ClienteJuridicoService mockService = (ClienteJuridicoService) serviceField.get(modal);
        verify(mockService).create(any(ClienteJuridicoCreateRequest.class));
    }

    @Test
    void testInvalidSubmitEmptyForm() throws Exception {
        var modal = new ClienteJuridicoCreateModal(MODAL);
        tester.startComponentInPage(modal);

        tester.executeAjaxEvent(MODAL + ":form:submit", "click");

        // Service should NOT have been called
        Field serviceField = ClienteJuridicoCreateModal.class.getDeclaredField("clienteJuridicoService");
        serviceField.setAccessible(true);
        ClienteJuridicoService mockService = (ClienteJuridicoService) serviceField.get(modal);
        verify(mockService, never()).create(any());

        // Form should have validation errors
        Form<?> form = (Form<?>) tester.getComponentFromLastRenderedPage(MODAL + ":form");
        assertTrue(form.hasError());
    }
}
