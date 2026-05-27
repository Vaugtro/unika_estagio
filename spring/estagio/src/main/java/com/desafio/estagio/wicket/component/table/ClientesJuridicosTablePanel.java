package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoListResponse;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.wicket.component.dataview.ClienteJuridicoDataView;
import com.desafio.estagio.wicket.component.modal.ClienteJuridicoCreateModal;
import com.desafio.estagio.wicket.component.modal.ExportModal;
import com.desafio.estagio.wicket.component.modal.ImportModal;
import com.desafio.estagio.wicket.provider.AbstractClienteDataProvider;
import com.desafio.estagio.wicket.provider.ClienteJuridicoDataProvider;
import org.apache.wicket.Component;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.InputStream;
import java.io.Serial;

public class ClientesJuridicosTablePanel extends ClientesTablePanel<ClienteJuridicoListResponse> {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteJuridicoService clienteJuridicoService;

    private ClienteJuridicoDataProvider dataProvider;

    public ClientesJuridicosTablePanel(String id) {
        super(id);
    }

    @Override
    protected AbstractClienteDataProvider<ClienteJuridicoListResponse> createDataProvider() {
        dataProvider = new ClienteJuridicoDataProvider(clienteJuridicoService);
        return dataProvider;
    }

    @Override
    protected DataView<ClienteJuridicoListResponse> createDataView(String id, IDataProvider<ClienteJuridicoListResponse> provider, long itemsPerPage) {
        return new ClienteJuridicoDataView(id, provider, itemsPerPage);
    }

    @Override
    protected Component createCreateModal(String id) {
        return new ClienteJuridicoCreateModal(id);
    }

    @Override
    protected ExportModal createExportModal(String id) {
        return new ExportModal(id, "exportJuridicoModal") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected byte[] getPdfData() {
                String q = dataProvider.getSearchQuery();
                return (q != null && !q.isBlank())
                        ? fileService.pdfJuridicosPorFiltro(q)
                        : fileService.pdfJuridicos();
            }

            @Override
            protected String getPdfName() {
                return "clientes-juridicos.pdf";
            }

            @Override
            protected byte[] getXlsxData() {
                String q = dataProvider.getSearchQuery();
                return (q != null && !q.isBlank())
                        ? fileService.xlsxJuridicosPorFiltro(q)
                        : fileService.xlsxJuridicos();
            }

            @Override
            protected String getXlsxName() {
                return "clientes-juridicos.xlsx";
            }
        };
    }

    @Override
    protected ImportModal createImportModal(String id) {
        return new ImportModal(id, "importJuridicoModal") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected byte[] getTemplateData() {
                return fileService.templateJuridicosImport();
            }

            @Override
            protected String getTemplateFileName() {
                return "template-clientes-juridicos.xlsx";
            }

            @Override
            protected int importData(InputStream is) throws Exception {
                return fileService.importJuridicos(is);
            }

            @Override
            protected String getSuccessMessage() {
                return "cliente(s) jurídico(s) importado(s) com sucesso!";
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
