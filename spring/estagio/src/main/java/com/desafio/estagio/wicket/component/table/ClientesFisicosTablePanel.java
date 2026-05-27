package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.service.FileService.ImportResult;
import com.desafio.estagio.wicket.component.dataview.ClienteFisicoDataView;
import com.desafio.estagio.wicket.component.modal.ClienteFisicoCreateModal;
import com.desafio.estagio.wicket.component.modal.ExportModal;
import com.desafio.estagio.wicket.component.modal.ImportModal;
import com.desafio.estagio.wicket.provider.AbstractClienteDataProvider;
import com.desafio.estagio.wicket.provider.ClienteFisicoDataProvider;
import org.apache.wicket.Component;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.spring.injection.annot.SpringBean;
import java.io.InputStream;
import java.io.Serial;

public class ClientesFisicosTablePanel extends ClientesTablePanel<ClienteFisicoListResponse> {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    private ClienteFisicoDataProvider dataProvider;

    public ClientesFisicosTablePanel(String id) {
        super(id);
    }

    @Override
    protected AbstractClienteDataProvider<ClienteFisicoListResponse> createDataProvider() {
        dataProvider = new ClienteFisicoDataProvider(clienteFisicoService);
        return dataProvider;
    }

    @Override
    protected DataView<ClienteFisicoListResponse> createDataView(String id, IDataProvider<ClienteFisicoListResponse> provider, long itemsPerPage) {
        return new ClienteFisicoDataView(id, provider, itemsPerPage);
    }

    @Override
    protected Component createCreateModal(String id) {
        return new ClienteFisicoCreateModal(id);
    }

    @Override
    protected ExportModal createExportModal(String id) {
        return new ExportModal(id, "exportFisicoModal") {
            @Serial
            private static final long serialVersionUID = 1L;
            @Override
            protected byte[] getPdfData() {
                String q = dataProvider.getSearchQuery();
                return (q != null && !q.isBlank()) ? fileService.pdfFisicosPorFiltro(q) : fileService.pdfFisicos();
            }
            @Override
            protected String getPdfName() {
                return "clientes-fisicos.pdf";
            }
            @Override
            protected byte[] getXlsxData() {
                String q = dataProvider.getSearchQuery();
                return (q != null && !q.isBlank()) ? fileService.xlsxFisicosPorFiltro(q) : fileService.xlsxFisicos();
            }
            @Override
            protected String getXlsxName() {
                return "clientes-fisicos.xlsx";
            }
        };
    }

    @Override
    protected ImportModal createImportModal(String id) {
        return new ImportModal(id, "importFisicoModal") {
            @Serial
            private static final long serialVersionUID = 1L;
            @Override
            protected byte[] getTemplateData() {
                return fileService.templateFisicosImport();
            }
            @Override
            protected String getTemplateFileName() {
                return "template-clientes-fisicos.xlsx";
            }
            @Override
            protected ImportResult importData(InputStream is) throws Exception {
                return fileService.importFisicos(is);
            }
            @Override
            protected String getSuccessMessage() {
                return "cliente(s) físico(s) importado(s) com sucesso!";
            }
        };
    }

    @Override
    protected String getSearchQuery() {
        return dataProvider.getSearchQuery();
    }

    @Override
    protected void setSearchQuery(String query) {
        dataProvider.setSearchQuery(query);
    }
}
