package com.desafio.estagio.wicket.component.shared;

import com.desafio.estagio.service.FileService;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.util.ByteArrayResourceStream;
import com.desafio.estagio.wicket.util.ErrorHandler;
import com.desafio.estagio.wicket.util.JavaScriptUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;

import java.io.InputStream;
import java.io.Serial;

public class EnderecoFilePanel extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;
    private final Long clienteId;
    private final WebMarkupContainer enderecosContainer;

    @SpringBean
    private FileService fileService;

    public EnderecoFilePanel(String id, Long clienteId, WebMarkupContainer enderecosContainer) {
        super(id);
        this.clienteId = clienteId;
        this.enderecosContainer = enderecosContainer;
        setOutputMarkupId(true);

        add(buildExportLink("exportEnderecosPdfBtn", "enderecos.pdf",
                "application/pdf", true));
        add(buildExportLink("exportEnderecosXlsxBtn", "enderecos.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", false));
        add(buildImportForm());
        add(buildTemplateLink("downloadEnderecoTemplateBtn"));
    }

    private Link<Void> buildExportLink(String id, String filename, String mimeType, boolean pdf) {
        return new Link<>(id) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                byte[] bytes = pdf
                        ? fileService.pdfEnderecos(clienteId)
                        : fileService.xlsxEnderecos(clienteId);
                IResourceStream stream = new ByteArrayResourceStream(bytes, mimeType);
                getRequestCycle().scheduleRequestHandlerAfterCurrent(
                        new ResourceStreamRequestHandler(stream)
                                .setFileName(filename)
                                .setContentDisposition(ContentDisposition.ATTACHMENT)
                );
            }
        };
    }

    private Form<Void> buildImportForm() {
        Form<Void> importForm = new Form<>("importEnderecoForm");
        importForm.setMultiPart(true);
        importForm.setOutputMarkupId(true);

        FileUploadField fileUpload = new FileUploadField("enderecoFileUpload");
        importForm.add(fileUpload);

        importForm.add(new AjaxButton("importEnderecoBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                FileUpload upload = fileUpload.getFileUpload();
                if (upload == null) {
                    ValidationFeedback.showToast(target, "error", "Selecione um arquivo XLSX.");
                    return;
                }
                ErrorHandler.handleFileOperation(target, () -> {
                    try (InputStream is = upload.getInputStream()) {
                        int count = fileService.importEnderecos(clienteId, is);
                        ValidationFeedback.showToast(target, "success",
                                count + " endereço(s) importado(s) com sucesso!");
                        target.add(enderecosContainer);
                        target.add(importForm);
                        JavaScriptUtils.reloadLucideIcons(target);
                    }
                });
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                ValidationFeedback.handleFormError(target, form);
            }
        });

        return importForm;
    }

    private Link<Void> buildTemplateLink(String id) {
        return new Link<>(id) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                byte[] bytes = fileService.templateEnderecosImport();
                IResourceStream stream = new ByteArrayResourceStream(bytes,
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                getRequestCycle().scheduleRequestHandlerAfterCurrent(
                        new ResourceStreamRequestHandler(stream)
                                .setFileName("template-enderecos.xlsx")
                                .setContentDisposition(ContentDisposition.ATTACHMENT)
                );
            }
        };
    }
}
