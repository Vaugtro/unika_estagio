package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.service.ExportService;
import com.desafio.estagio.wicket.component.dataview.ClienteFisicoDataView;
import com.desafio.estagio.wicket.component.modal.ClienteFisicoCreateModal;
import com.desafio.estagio.wicket.provider.ClienteFisicoDataProvider;
import com.desafio.estagio.wicket.util.ByteArrayResourceStream;
import org.apache.wicket.devutils.DevUtilsPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;

import java.io.Serial;

public class ClientesFisicosTablePanel extends DevUtilsPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    @SpringBean
    private ExportService exportService;

    public ClientesFisicosTablePanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        WebMarkupContainer tableContainer = new WebMarkupContainer("tableContainer");
        tableContainer.setOutputMarkupId(true);
        add(tableContainer);

        IDataProvider<ClienteFisicoListResponse> dataProvider = new ClienteFisicoDataProvider(clienteFisicoService);
        DataView<ClienteFisicoListResponse> dataView = new ClienteFisicoDataView("rows", dataProvider, 10);
        tableContainer.add(dataView);

        AjaxPagingNavigator navigator = new AjaxPagingNavigator("navigator", dataView);
        navigator.setOutputMarkupId(true);
        add(navigator);

        add(buildExportLink("exportPdfBtn", "clientes-fisicos.pdf", "application/pdf", true));
        add(buildExportLink("exportXlsxBtn", "clientes-fisicos.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", false));

        add(new ClienteFisicoCreateModal("createModal"));
    }

    private Link<Void> buildExportLink(String id, String filename, String mimeType, boolean pdf) {
        return new Link<>(id) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                byte[] bytes = pdf ? exportService.pdfFisicos() : exportService.xlsxFisicos();
                IResourceStream stream = new ByteArrayResourceStream(bytes, mimeType);
                getRequestCycle().scheduleRequestHandlerAfterCurrent(
                        new ResourceStreamRequestHandler(stream)
                                .setFileName(filename)
                                .setContentDisposition(ContentDisposition.ATTACHMENT)
                );
            }
        };
    }
}