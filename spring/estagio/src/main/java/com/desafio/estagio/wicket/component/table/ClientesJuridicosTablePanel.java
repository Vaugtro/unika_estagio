package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoListResponse;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.service.FileService;
import com.desafio.estagio.wicket.component.dataview.ClienteJuridicoDataView;
import com.desafio.estagio.wicket.component.modal.ClienteJuridicoCreateModal;
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

    @SpringBean
    private FileService fileService;

    public ClientesJuridicosTablePanel(String id) {
        super(id);
    }

    @Override
    protected ClienteJuridicoDataProvider createDataProvider() {
        return new ClienteJuridicoDataProvider(clienteJuridicoService);
    }

    @Override
    protected DataView<ClienteJuridicoListResponse> createDataView(String id, IDataProvider<ClienteJuridicoListResponse> provider) {
        return new ClienteJuridicoDataView(id, provider, 10);
    }

    @Override
    protected Component createCreateModal(String id) {
        return new ClienteJuridicoCreateModal(id);
    }

    // --- Export ---

    @Override
    protected byte[] supplyPdfData(String query) {
        return (query != null && !query.isBlank())
                ? fileService.pdfJuridicosPorFiltro(query)
                : fileService.pdfJuridicos();
    }

    @Override
    protected byte[] supplyXlsxData(String query) {
        return (query != null && !query.isBlank())
                ? fileService.xlsxJuridicosPorFiltro(query)
                : fileService.xlsxJuridicos();
    }

    @Override
    protected String getPdfFileName() {
        return "clientes-juridicos.pdf";
    }

    @Override
    protected String getXlsxFileName() {
        return "clientes-juridicos.xlsx";
    }

    @Override
    protected String getExportModalWindowId() {
        return "exportJuridicoModal";
    }

    // --- Import ---

    @Override
    protected byte[] supplyImportTemplate() {
        return fileService.templateJuridicosImport();
    }

    @Override
    protected String getImportTemplateFileName() {
        return "template-clientes-juridicos.xlsx";
    }

    @Override
    protected int processImportData(InputStream is) throws Exception {
        return fileService.importJuridicos(is);
    }

    @Override
    protected String getImportSuccessMessage() {
        return "cliente(s) jurídico(s) importado(s) com sucesso!";
    }

    @Override
    protected String getImportModalWindowId() {
        return "importJuridicoModal";
    }
}
