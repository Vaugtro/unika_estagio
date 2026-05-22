package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.wicket.util.ByteArrayResourceStream;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.IResourceStream;

import java.io.Serial;

public abstract class ExportModal extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;

    public ExportModal(String id, String modalHtmlId) {
        super(id);
        WebMarkupContainer modalRoot = new WebMarkupContainer("modalRoot");
        modalRoot.add(new AttributeModifier("id", modalHtmlId));
        add(modalRoot);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        WebMarkupContainer modalRoot = (WebMarkupContainer) get("modalRoot");

        modalRoot.add(new Link<Void>("exportPdf") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                byte[] bytes = getPdfData();
                IResourceStream stream = new ByteArrayResourceStream(bytes, "application/pdf");
                getRequestCycle().scheduleRequestHandlerAfterCurrent(
                        new ResourceStreamRequestHandler(stream)
                                .setFileName(getPdfName())
                                .setContentDisposition(ContentDisposition.ATTACHMENT)
                );
            }
        });

        modalRoot.add(new Link<Void>("exportXlsx") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                byte[] bytes = getXlsxData();
                IResourceStream stream = new ByteArrayResourceStream(bytes,
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                getRequestCycle().scheduleRequestHandlerAfterCurrent(
                        new ResourceStreamRequestHandler(stream)
                                .setFileName(getXlsxName())
                                .setContentDisposition(ContentDisposition.ATTACHMENT)
                );
            }
        });
    }

    protected abstract byte[] getPdfData();
    protected abstract String getPdfName();
    protected abstract byte[] getXlsxData();
    protected abstract String getXlsxName();
}
