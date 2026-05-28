package com.desafio.estagio.wicket.component.shared;

import com.desafio.estagio.wicket.WicketTestBase;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.junit.jupiter.api.Test;

class EnderecoListViewPanelTest extends WicketTestBase {

    @Test
    void panelRendersWithoutErrors() {
        tester.startComponentInPage(new EnderecoListViewPanel("panel", 1L));
        tester.assertNoErrorMessage();
    }

    @Test
    void exportPdfButtonExistsAndIsVisible() {
        tester.startComponentInPage(new EnderecoListViewPanel("panel", 1L));
        tester.assertComponent("panel:exportEnderecosPdfBtn", Link.class);
        tester.assertVisible("panel:exportEnderecosPdfBtn");
    }

    @Test
    void exportXlsxButtonExistsAndIsVisible() {
        tester.startComponentInPage(new EnderecoListViewPanel("panel", 1L));
        tester.assertComponent("panel:exportEnderecosXlsxBtn", Link.class);
        tester.assertVisible("panel:exportEnderecosXlsxBtn");
    }

    @Test
    void downloadTemplateLinkExistsAndIsVisible() {
        tester.startComponentInPage(new EnderecoListViewPanel("panel", 1L));
        tester.assertComponent("panel:downloadEnderecoTemplateBtn", Link.class);
        tester.assertVisible("panel:downloadEnderecoTemplateBtn");
    }

    @Test
    void adicionarEnderecoButtonExistsAndIsVisible() {
        tester.startComponentInPage(new EnderecoListViewPanel("panel", 1L));
        tester.assertComponent("panel:adicionarEnderecoBtn", AjaxLink.class);
        tester.assertVisible("panel:adicionarEnderecoBtn");
    }

    @Test
    void importFormContainsFileUploadAndImportButton() {
        tester.startComponentInPage(new EnderecoListViewPanel("panel", 1L));
        tester.assertComponent("panel:importEnderecoForm", Form.class);
        tester.assertComponent("panel:importEnderecoForm:enderecoFileUpload", FileUploadField.class);
        tester.assertComponent("panel:importEnderecoForm:importEnderecoBtn", AjaxButton.class);
    }

    @Test
    void modalFormRendersWithSaveButton() {
        tester.startComponentInPage(new EnderecoListViewPanel("panel", 1L));
        tester.assertComponent("panel:modalForm", Form.class);
        tester.assertComponent("panel:modalForm:salvarEnderecoBtn", AjaxButton.class);
    }
}
