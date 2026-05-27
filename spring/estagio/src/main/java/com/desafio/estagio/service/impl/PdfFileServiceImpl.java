package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoReportResponse;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoReportResponse;
import com.desafio.estagio.dto.endereco.EnderecoResponse;
import com.desafio.estagio.exceptions.ResourceNotFoundException;
import com.desafio.estagio.model.Cliente;
import com.desafio.estagio.model.ClienteFisico;
import com.desafio.estagio.model.ClienteJuridico;
import com.desafio.estagio.repository.ClienteRepository;
import com.desafio.estagio.service.*;
import com.desafio.estagio.service.FileService.ImportResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PdfFileServiceImpl implements FileService {

    private final ClienteFisicoService clienteFisicoService;
    private final ClienteJuridicoService clienteJuridicoService;
    private final EnderecoService enderecoService;
    private final JasperReportService jasperReportService;
    private final XlsxFileServiceImpl xlsxFileService;
    private final ClienteRepository<Cliente> clienteRepository;

    // =====================================================================
    // PDF — delegates to JasperReportService (uses compiled .jrxml templates)
    // NOTE: DTOs are Java records. JasperReports + Commons BeanUtils cannot
    // introspect records (no getX() methods), so we convert records -> Map
    // and use JRMapCollectionDataSource instead of JRBeanCollectionDataSource.
    // =====================================================================

    @Override
    public byte[] pdfFisicos() {
        List<ClienteFisicoReportResponse> data = resolveFisicoData(null);
        return pdfFromRecords("ClienteFisicoReport", data);
    }

    @Override
    public byte[] pdfFisicosPorFiltro(String searchQuery) {
        List<ClienteFisicoReportResponse> data = resolveFisicoData(searchQuery);
        return pdfFromRecords("ClienteFisicoReport", data);
    }

    @Override
    public byte[] pdfJuridicos() {
        List<ClienteJuridicoReportResponse> data = resolveJuridicoData(null);
        return pdfFromRecords("ClienteJuridicoReport", data);
    }

    @Override
    public byte[] pdfJuridicosPorFiltro(String searchQuery) {
        List<ClienteJuridicoReportResponse> data = resolveJuridicoData(searchQuery);
        return pdfFromRecords("ClienteJuridicoReport", data);
    }

    @Override
    public byte[] pdfEnderecos(Long clienteId) {
        List<EnderecoResponse> enderecos = enderecoService.findAllByClienteId(clienteId);

        List<Map<String, ?>> enderecoMaps = enderecos.stream()
                .map(this::recordToMap)
                .collect(Collectors.toList());

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente não encontrado: " + clienteId));

        Map<String, Object> params = new HashMap<>();
        params.put("TABLE_DATA_SOURCE", new JRMapCollectionDataSource(enderecoMaps));

        if (cliente instanceof ClienteFisico cf) {
            params.put("CLIENTE_NOME", cf.getNome());
            params.put("CLIENTE_DOCUMENTO", cf.getCpf());
            params.put("CLIENTE_DOC_LABEL", "CPF");
        } else if (cliente instanceof ClienteJuridico cj) {
            params.put("CLIENTE_NOME", cj.getRazaoSocial());
            params.put("CLIENTE_DOCUMENTO", cj.getCnpj());
            params.put("CLIENTE_DOC_LABEL", "CNPJ");
        }
        params.put("CLIENTE_EMAIL", cliente.getEmail());

        return jasperReportService.generatePdfWithDataSource(
                "EnderecoReport", new JREmptyDataSource(), params);
    }

    /**
     * Converts a list of Java records to Maps and generates a PDF report.
     * JasperReports + Commons BeanUtils cannot introspect records, so we
     * bypass JRBeanCollectionDataSource in favour of JRMapCollectionDataSource.
     */
    private byte[] pdfFromRecords(String reportName, List<?> records) {
        List<Map<String, ?>> maps = records.stream()
                .map(this::recordToMap)
                .collect(Collectors.toList());

        Map<String, Object> params = new HashMap<>();
        params.put("TABLE_DATA_SOURCE", new JRMapCollectionDataSource(maps));

        return jasperReportService.generatePdfWithDataSource(
                reportName, new JREmptyDataSource(), params);
    }

    /**
     * Converts any Java record to a Map using its RecordComponent accessors.
     */
    private Map<String, ?> recordToMap(Object record) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (var rc : record.getClass().getRecordComponents()) {
            try {
                map.put(rc.getName(), rc.getAccessor().invoke(record));
            } catch (Exception e) {
                map.put(rc.getName(), null);
            }
        }
        return map;
    }

    // =====================================================================
    // XLSX — delegates to XlsxFileService
    // =====================================================================

    @Override
    public byte[] xlsxFisicos() {
        return xlsxFileService.xlsxFisicos();
    }

    @Override
    public byte[] xlsxFisicosPorFiltro(String searchQuery) {
        return xlsxFileService.xlsxFisicosPorFiltro(searchQuery);
    }

    @Override
    public byte[] xlsxJuridicos() {
        return xlsxFileService.xlsxJuridicos();
    }

    @Override
    public byte[] xlsxJuridicosPorFiltro(String searchQuery) {
        return xlsxFileService.xlsxJuridicosPorFiltro(searchQuery);
    }

    @Override
    public byte[] xlsxEnderecos(Long clienteId) {
        return xlsxFileService.xlsxEnderecos(clienteId);
    }

    private List<ClienteFisicoReportResponse> resolveFisicoData(String searchQuery) {
        if (searchQuery != null && !searchQuery.isBlank()) {
            return clienteFisicoService
                    .searchForReport(searchQuery, PageRequest.of(0, Integer.MAX_VALUE))
                    .getContent();
        }
        return clienteFisicoService
                .findAllForReport(PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();
    }

    private List<ClienteJuridicoReportResponse> resolveJuridicoData(String searchQuery) {
        if (searchQuery != null && !searchQuery.isBlank()) {
            return clienteJuridicoService
                    .searchForReport(searchQuery, PageRequest.of(0, Integer.MAX_VALUE))
                    .getContent();
        }
        return clienteJuridicoService
                .findAllForReport(PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();
    }

    // =====================================================================
    // IMPORT TEMPLATES
    // =====================================================================

    @Override
    public byte[] templateFisicosImport() {
        return xlsxFileService.templateFisicosImport();
    }

    @Override
    public byte[] templateJuridicosImport() {
        return xlsxFileService.templateJuridicosImport();
    }

    @Override
    public byte[] templateEnderecosImport() {
        return xlsxFileService.templateEnderecosImport();
    }

    // =====================================================================
    // XLSX IMPORT
    // =====================================================================

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ImportResult importFisicos(java.io.InputStream xlsx) {
        return xlsxFileService.importFisicos(xlsx);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ImportResult importJuridicos(java.io.InputStream xlsx) {
        return xlsxFileService.importJuridicos(xlsx);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ImportResult importEnderecos(Long clienteId, java.io.InputStream xlsx) {
        return xlsxFileService.importEnderecos(clienteId, xlsx);
    }
}
