package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoReportResponse;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoReportResponse;
import com.desafio.estagio.dto.endereco.EnderecoResponse;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.service.EnderecoService;
import com.desafio.estagio.service.FileService;
import com.desafio.estagio.service.JasperReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileServiceImpl implements FileService {

    private final ClienteFisicoService clienteFisicoService;
    private final ClienteJuridicoService clienteJuridicoService;
    private final EnderecoService enderecoService;
    private final JasperReportService jasperReportService;
    private final XlsxFileService xlsxFileService;

    // =====================================================================
    // PDF — delegates to JasperReportService (uses compiled .jrxml templates)
    // NOTE: DTOs are Java records. JasperReports + Commons BeanUtils cannot
    // introspect records (no getX() methods), so we convert records -> Map
    // and use JRMapCollectionDataSource instead of JRBeanCollectionDataSource.
    // =====================================================================

    @Override
    public byte[] pdfFisicos() {
        List<ClienteFisicoReportResponse> data = clienteFisicoService
                .findAllForReport(PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();
        return pdfFromRecords("ClienteFisicoReport", data);
    }

    @Override
    public byte[] pdfJuridicos() {
        List<ClienteJuridicoReportResponse> data = clienteJuridicoService
                .findAllForReport(PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();
        return pdfFromRecords("ClienteJuridicoReport", data);
    }

    @Override
    public byte[] pdfEnderecos(Long clienteId) {
        List<EnderecoResponse> enderecos = enderecoService.findAllByClienteId(clienteId);
        return pdfFromRecords("EnderecoReport", enderecos);
    }

    /**
     * Converts a list of Java records to Maps and generates a PDF report.
     * JasperReports + Commons BeanUtils cannot introspect records, so we
     * bypass JRBeanCollectionDataSource in favour of JRMapCollectionDataSource.
     */
    private byte[] pdfFromRecords(String reportName, List<?> records) {
        var maps = records.stream()
                .map(this::recordToMap)
                .collect(Collectors.<Map<String, ?>>toList());

        Map<String, Object> params = new HashMap<>();
        params.put("TABLE_DATA_SOURCE", new JRMapCollectionDataSource(maps));

        return jasperReportService.generatePdfWithDataSource(
                reportName, new JRMapCollectionDataSource(maps), params);
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
    public byte[] xlsxJuridicos() {
        return xlsxFileService.xlsxJuridicos();
    }

    @Override
    public byte[] xlsxEnderecos(Long clienteId) {
        return xlsxFileService.xlsxEnderecos(clienteId);
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
    @Transactional
    public int importFisicos(java.io.InputStream xlsx) {
        return xlsxFileService.importFisicos(xlsx);
    }

    @Override
    @Transactional
    public int importJuridicos(java.io.InputStream xlsx) {
        return xlsxFileService.importJuridicos(xlsx);
    }

    @Override
    @Transactional
    public int importEnderecos(Long clienteId, java.io.InputStream xlsx) {
        return xlsxFileService.importEnderecos(clienteId, xlsx);
    }
}
