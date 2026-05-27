package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.wicket.builder.AttributeModifierBuilder;
import com.desafio.estagio.exceptions.BusinessException;
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
import org.apache.wicket.util.resource.IResourceStream;

import java.io.InputStream;
import java.io.Serial;

public abstract class ImportModal extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;

    public ImportModal(String id, String modalHtmlId) {
        super(id);
        WebMarkupContainer modalRoot = new WebMarkupContainer("modalRoot");
        AttributeModifierBuilder.create().attribute("id", modalHtmlId).buildAndAdd(modalRoot);
        add(modalRoot);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        WebMarkupContainer modalRoot = (WebMarkupContainer) get("modalRoot");

        modalRoot.add(new Link<Void>("downloadTemplate") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                byte[] bytes = getTemplateData();
                IResourceStream stream = new ByteArrayResourceStream(bytes,
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                getRequestCycle().scheduleRequestHandlerAfterCurrent(
                        new ResourceStreamRequestHandler(stream)
                                .setFileName(getTemplateFileName())
                                .setContentDisposition(ContentDisposition.ATTACHMENT)
                );
            }
        });

        Form<Void> importForm = new Form<>("importForm");
        importForm.setMultiPart(true);
        importForm.setOutputMarkupId(true);
        modalRoot.add(importForm);

        FileUploadField fileUpload = new FileUploadField("fileUpload");
        importForm.add(fileUpload);

        importForm.add(new AjaxButton("importBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                FileUpload upload = fileUpload.getFileUpload();
                if (upload == null) {
                    ValidationFeedback.showToast(target, "error", "Selecione um arquivo XLSX.");
                    return;
                }
                ErrorHandler.handleServiceCall(() -> {
                    try (InputStream is = upload.getInputStream()) {
                        int count = importData(is);
                        ValidationFeedback.showToast(target, "success",
                                count + " " + getSuccessMessage());
                        JavaScriptUtils.reloadAfterDelay(target, 3000);
                    } catch (Exception ex) {
                        String causeMsg = ex.getMessage();
                        if (causeMsg != null && causeMsg.contains("rolled back")) {
                            throw new BusinessException("Erro na importação: Verifique os dados do arquivo e tente novamente.");
                        }
                        throw new BusinessException("Erro na importação: " + causeMsg);
                    }
                }, target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                ValidationFeedback.handleFormError(target, form);
            }
        });
    }

    protected abstract byte[] getTemplateData();

    protected abstract String getTemplateFileName();

    protected abstract int importData(InputStream is) throws Exception;

    protected abstract String getSuccessMessage();
}
