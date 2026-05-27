package com.desafio.estagio.wicket.component.shared;

import com.desafio.estagio.dto.endereco.EnderecoResponse;
import com.desafio.estagio.service.EnderecoService;
import com.desafio.estagio.service.FileService;
import com.desafio.estagio.wicket.WicketTestBase;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Mockito.when;

class EnderecoListViewPanelTest extends WicketTestBase {

    @Mock
    private EnderecoService enderecoService;

    @Mock
    private FileService fileService;

    private static final Long CLIENTE_ID = 1L;
    private static final String PANEL_PATH = "panel";

    @BeforeEach
    void setUpBeans() {
        // Wicket SpringComponentInjector uses getBeanNamesForType to discover beans,
        // then getBean(name, type) to resolve the @SpringBean proxy.
        when(applicationContext.getBeanNamesForType(EnderecoService.class))
                .thenReturn(new String[]{"enderecoService"});
        when(applicationContext.getBean("enderecoService", EnderecoService.class))
                .thenReturn(enderecoService);

        when(applicationContext.getBeanNamesForType(FileService.class))
                .thenReturn(new String[]{"fileService"});
        when(applicationContext.getBean("fileService", FileService.class))
                .thenReturn(fileService);
    }

    @Test
    void rendersPanelWithoutErrors() {
        when(enderecoService.findAllByClienteId(CLIENTE_ID)).thenReturn(List.of());

        var panel = new EnderecoListViewPanel(PANEL_PATH, CLIENTE_ID);
        tester.startComponentInPage(panel);

        tester.assertNoErrorMessage();
        tester.assertComponent(PANEL_PATH, EnderecoListViewPanel.class);
    }

    @Test
    void rendersEnderecoRowsWithData() {
        var enderecos = List.of(
                EnderecoResponse.builder().id(1L).logradouro("Rua A").numero(123L)
                        .bairro("Centro").cep("01001-000").cidade("São Paulo")
                        .estado("SP").telefone("11999999999").principal(true).build(),
                EnderecoResponse.builder().id(2L).logradouro("Rua B").numero(456L)
                        .bairro("Bairro B").cep("02002-000").cidade("Rio de Janeiro")
                        .estado("RJ").telefone("21999999999").principal(false).build()
        );
        when(enderecoService.findAllByClienteId(CLIENTE_ID)).thenReturn(enderecos);

        var panel = new EnderecoListViewPanel(PANEL_PATH, CLIENTE_ID);
        tester.startComponentInPage(panel);

        tester.assertNoErrorMessage();
        tester.assertLabel(PANEL_PATH + ":enderecosContainer:enderecoRow:0:logradouro", "Rua A");
        tester.assertLabel(PANEL_PATH + ":enderecosContainer:enderecoRow:0:numero", "123");
        tester.assertLabel(PANEL_PATH + ":enderecosContainer:enderecoRow:1:logradouro", "Rua B");
    }

    @Test
    void rendersModalFormComponents() {
        when(enderecoService.findAllByClienteId(CLIENTE_ID)).thenReturn(List.of());

        var panel = new EnderecoListViewPanel(PANEL_PATH, CLIENTE_ID);
        tester.startComponentInPage(panel);

        tester.assertNoErrorMessage();
        tester.assertComponent(PANEL_PATH + ":modalForm", Form.class);
        tester.assertComponent(PANEL_PATH + ":modalForm:enderecoModalLabel", Label.class);
        tester.assertComponent(PANEL_PATH + ":modalForm:salvarEnderecoBtn", AjaxButton.class);
    }

    @Test
    void rendersFilePanelWithExportImportLinks() {
        when(enderecoService.findAllByClienteId(CLIENTE_ID)).thenReturn(List.of());

        var panel = new EnderecoListViewPanel(PANEL_PATH, CLIENTE_ID);
        tester.startComponentInPage(panel);

        tester.assertNoErrorMessage();
        tester.assertComponent(PANEL_PATH + ":filePanel", EnderecoFilePanel.class);
        tester.assertComponent(PANEL_PATH + ":filePanel:exportEnderecosPdfBtn", Link.class);
        tester.assertComponent(PANEL_PATH + ":filePanel:exportEnderecosXlsxBtn", Link.class);
        tester.assertComponent(PANEL_PATH + ":filePanel:downloadEnderecoTemplateBtn", Link.class);
        tester.assertComponent(PANEL_PATH + ":filePanel:importEnderecoForm", Form.class);
        tester.assertComponent(PANEL_PATH + ":filePanel:importEnderecoForm:importEnderecoBtn",
                AjaxButton.class);
    }

    @Test
    void rendersAdicionarButton() {
        when(enderecoService.findAllByClienteId(CLIENTE_ID)).thenReturn(List.of());

        var panel = new EnderecoListViewPanel(PANEL_PATH, CLIENTE_ID);
        tester.startComponentInPage(panel);

        tester.assertNoErrorMessage();
        tester.assertComponent(PANEL_PATH + ":adicionarEnderecoBtn", AjaxLink.class);
    }

    @Test
    void adicionarEnderecoInsertsModalRow() {
        when(enderecoService.findAllByClienteId(CLIENTE_ID)).thenReturn(List.of());

        var panel = new EnderecoListViewPanel(PANEL_PATH, CLIENTE_ID);
        tester.startComponentInPage(panel);

        tester.executeAjaxEvent(PANEL_PATH + ":adicionarEnderecoBtn", "click");

        tester.assertNoErrorMessage();
        tester.assertComponent(
                PANEL_PATH + ":modalForm:enderecoTablePanel:enderecosRow:0:logradouro",
                TextField.class);
    }
}
