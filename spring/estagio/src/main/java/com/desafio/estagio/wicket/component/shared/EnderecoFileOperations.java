package com.desafio.estagio.wicket.component.shared;

import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.service.FileService;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.util.ByteArrayResourceStream;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.IResourceStream;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.Serial;
import java.io.Serializable;

public final class EnderecoFileOperations implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private EnderecoFileOperations() {
    }

    public static Link<Void> buildExportLink(String id, String filename, String mimeType, boolean pdf,
                                              FileService fileService, Long clienteId) {
        return new Link<>(id) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                byte[] bytes = pdf ? fileService.pdfEnderecos(clienteId) : fileService.xlsxEnderecos(clienteId);
                IResourceStream stream = new ByteArrayResourceStream(bytes, mimeType);
                getRequestCycle().scheduleRequestHandlerAfterCurrent(
                        new ResourceStreamRequestHandler(stream)
                                .setFileName(filename)
                                .setContentDisposition(ContentDisposition.ATTACHMENT)
                );
            }
        };
    }

    public static Form<Void> buildImportForm(WebMarkupContainer enderecosContainer,
                                              FileService fileService, Long clienteId) {
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
                try (java.io.InputStream is = upload.getInputStream()) {
                    int count = fileService.importEnderecos(clienteId, is);
                    ValidationFeedback.showToast(target, "success",
                            count + " endereço(s) importado(s) com sucesso!");
                    target.add(enderecosContainer);
                    target.add(importForm);
                    target.appendJavaScript("lucide.createIcons();");
                } catch (DataIntegrityViolationException e) {
                    ValidationFeedback.showToast(target, "error",
                            "Já existe um endereço principal para este cliente.");
                } catch (BusinessException e) {
                    ValidationFeedback.showToast(target, "error", e.getMessage());
                } catch (Exception e) {
                    ValidationFeedback.showToast(target, "error",
                            "Erro na importação. Tente novamente.");
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                ValidationFeedback.handleFormError(target, form);
            }
        });

        return importForm;
    }

    public static Link<Void> buildTemplateLink(String id, FileService fileService) {
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
