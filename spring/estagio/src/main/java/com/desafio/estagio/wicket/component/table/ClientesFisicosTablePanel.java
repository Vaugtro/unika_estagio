package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.service.FileService;
import com.desafio.estagio.wicket.component.dataview.ClienteFisicoDataView;
import com.desafio.estagio.wicket.component.modal.ClienteFisicoCreateModal;
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

    @SpringBean
    private FileService fileService;

    public ClientesFisicosTablePanel(String id) {
        super(id);
    }

    @Override
    protected ClienteFisicoDataProvider createDataProvider() {
        return new ClienteFisicoDataProvider(clienteFisicoService);
    }

    @Override
    protected DataView<ClienteFisicoListResponse> createDataView(String id, IDataProvider<ClienteFisicoListResponse> provider) {
        return new ClienteFisicoDataView(id, provider, 10);
    }

    @Override
    protected Component createCreateModal(String id) {
        return new ClienteFisicoCreateModal(id);
    }

    // --- Export ---

    @Override
    protected byte[] supplyPdfData(String query) {
        return (query != null && !query.isBlank())
                ? fileService.pdfFisicosPorFiltro(query)
                : fileService.pdfFisicos();
    }

    @Override
    protected byte[] supplyXlsxData(String query) {
        return (query != null && !query.isBlank())
                ? fileService.xlsxFisicosPorFiltro(query)
                : fileService.xlsxFisicos();
    }

    @Override
    protected String getPdfFileName() {
        return "clientes-fisicos.pdf";
    }

    @Override
    protected String getXlsxFileName() {
        return "clientes-fisicos.xlsx";
    }

    @Override
    protected String getExportModalWindowId() {
        return "exportFisicoModal";
    }

    // --- Import ---

    @Override
    protected byte[] supplyImportTemplate() {
        return fileService.templateFisicosImport();
    }

    @Override
    protected String getImportTemplateFileName() {
        return "template-clientes-fisicos.xlsx";
    }

    @Override
    protected int processImportData(InputStream is) throws Exception {
        return fileService.importFisicos(is);
    }

    @Override
    protected String getImportSuccessMessage() {
        return "cliente(s) físico(s) importado(s) com sucesso!";
    }

    @Override
    protected String getImportModalWindowId() {
        return "importFisicoModal";
    }
}
