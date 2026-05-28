package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.model.Municipio;
import com.desafio.estagio.model.UnidadeFederativa;
import com.desafio.estagio.repository.MunicipioRepository;
import com.desafio.estagio.repository.UnidadeFederativaRepository;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoCreateRequest;
import com.desafio.estagio.service.ClienteFisicoService;
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

class ClienteFisicoCreateModalTest extends WicketTestBase {

    private static final String MODAL = "modal";

    private static final List<UnidadeFederativa> ALL_UFS = List.of(
            new UnidadeFederativa(12L, "AC", "Acre"), new UnidadeFederativa(27L, "AL", "Alagoas"),
            new UnidadeFederativa(16L, "AP", "Amapá"), new UnidadeFederativa(13L, "AM", "Amazonas"),
            new UnidadeFederativa(29L, "BA", "Bahia"), new UnidadeFederativa(23L, "CE", "Ceará"),
            new UnidadeFederativa(53L, "DF", "Distrito Federal"), new UnidadeFederativa(32L, "ES", "Espírito Santo"),
            new UnidadeFederativa(52L, "GO", "Goiás"), new UnidadeFederativa(21L, "MA", "Maranhão"),
            new UnidadeFederativa(51L, "MT", "Mato Grosso"), new UnidadeFederativa(50L, "MS", "Mato Grosso do Sul"),
            new UnidadeFederativa(31L, "MG", "Minas Gerais"), new UnidadeFederativa(15L, "PA", "Pará"),
            new UnidadeFederativa(25L, "PB", "Paraíba"), new UnidadeFederativa(41L, "PR", "Paraná"),
            new UnidadeFederativa(26L, "PE", "Pernambuco"), new UnidadeFederativa(22L, "PI", "Piauí"),
            new UnidadeFederativa(33L, "RJ", "Rio de Janeiro"), new UnidadeFederativa(24L, "RN", "Rio Grande do Norte"),
            new UnidadeFederativa(43L, "RS", "Rio Grande do Sul"), new UnidadeFederativa(11L, "RO", "Rondônia"),
            new UnidadeFederativa(14L, "RR", "Roraima"), new UnidadeFederativa(42L, "SC", "Santa Catarina"),
            new UnidadeFederativa(35L, "SP", "São Paulo"), new UnidadeFederativa(28L, "SE", "Sergipe"),
            new UnidadeFederativa(17L, "TO", "Tocantins")
    );

    @BeforeEach
    void configureMocks() {
        var ufRepo = mock(UnidadeFederativaRepository.class);
        when(ufRepo.findAll()).thenReturn(ALL_UFS);
        configureMock(UnidadeFederativaRepository.class, ufRepo);

        var municipioRepo = mock(MunicipioRepository.class);
        when(municipioRepo.findByUnidadeFederativaSiglaOrderByNome(any())).thenReturn(List.of());
        configureMock(MunicipioRepository.class, municipioRepo);
    }

    @Test
    void testRender() {
        var modal = new ClienteFisicoCreateModal(MODAL);
        tester.startComponentInPage(modal);

        tester.assertComponent(MODAL, ClienteFisicoCreateModal.class);
        tester.assertComponent(MODAL + ":form", Form.class);

        // Form fields
        tester.assertComponent(MODAL + ":form:cpf", TextField.class);
        tester.assertComponent(MODAL + ":form:nome", TextField.class);
        tester.assertComponent(MODAL + ":form:rg", TextField.class);
        tester.assertComponent(MODAL + ":form:email", TextField.class);
        tester.assertComponent(MODAL + ":form:dataNascimento", TextField.class);

        // Feedback labels
        tester.assertComponent(MODAL + ":form:cpfFeedback", Label.class);
        tester.assertComponent(MODAL + ":form:nomeFeedback", Label.class);
        tester.assertComponent(MODAL + ":form:rgFeedback", Label.class);
        tester.assertComponent(MODAL + ":form:emailFeedback", Label.class);
        tester.assertComponent(MODAL + ":form:dataNascimentoFeedback", Label.class);

        // Endereco table panel
        tester.assertComponent(MODAL + ":form:enderecosContainer",
                com.desafio.estagio.wicket.component.shared.EnderecoCreateTablePanel.class);

        // First endereco row fields
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:logradouro", TextField.class);
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:numero", TextField.class);
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:bairro", TextField.class);
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:cep", TextField.class);
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:estado", DropDownChoice.class);
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:municipioId", DropDownChoice.class);
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:telefone", TextField.class);
        tester.assertComponent(MODAL + ":form:enderecosContainer:enderecosRow:0:complemento", TextField.class);

        // Submit button
        tester.assertComponent(MODAL + ":form:submit",
                org.apache.wicket.ajax.markup.html.form.AjaxButton.class);

        tester.assertNoErrorMessage();
    }

    @Test
    void testValidSubmit() throws Exception {
        var modal = new ClienteFisicoCreateModal(MODAL);
        tester.startComponentInPage(modal);

        FormTester formTester = tester.newFormTester(MODAL + ":form");
        formTester.setValue("cpf", "529.982.247-25");
        formTester.setValue("nome", "João Silva");
        formTester.setValue("rg", "12.345.678-9");
        formTester.setValue("email", "joao@test.com");
        formTester.setValue("dataNascimento", "15/05/1990");

        // Endereco 0 required fields
        formTester.setValue("enderecosContainer:enderecosRow:0:logradouro", "Rua Teste");
        formTester.setValue("enderecosContainer:enderecosRow:0:numero", "123");
        formTester.setValue("enderecosContainer:enderecosRow:0:bairro", "Centro");
        formTester.setValue("enderecosContainer:enderecosRow:0:cep", "12345-678");
        formTester.setValue("enderecosContainer:enderecosRow:0:estado", "SP");

        tester.executeAjaxEvent(MODAL + ":form:submit", "click");

        Field serviceField = ClienteFisicoCreateModal.class.getDeclaredField("clienteFisicoService");
        serviceField.setAccessible(true);
        ClienteFisicoService mockService = (ClienteFisicoService) serviceField.get(modal);
        verify(mockService).create(any(ClienteFisicoCreateRequest.class));
    }

    @Test
    void testInvalidSubmitEmptyForm() throws Exception {
        var modal = new ClienteFisicoCreateModal(MODAL);
        tester.startComponentInPage(modal);

        tester.executeAjaxEvent(MODAL + ":form:submit", "click");

        // Service should NOT have been called
        Field serviceField = ClienteFisicoCreateModal.class.getDeclaredField("clienteFisicoService");
        serviceField.setAccessible(true);
        ClienteFisicoService mockService = (ClienteFisicoService) serviceField.get(modal);
        verify(mockService, never()).create(any());

        // Form should have validation errors
        Form<?> form = (Form<?>) tester.getComponentFromLastRenderedPage(MODAL + ":form");
        assertTrue(form.hasError());
    }
}
