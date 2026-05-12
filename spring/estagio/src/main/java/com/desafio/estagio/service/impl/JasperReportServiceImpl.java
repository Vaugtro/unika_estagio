package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.ClienteFisicoDTO;
import com.desafio.estagio.dto.ClienteJuridicoDTO;
import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.model.ClienteFisico;
import com.desafio.estagio.model.ClienteJuridico;
import com.desafio.estagio.model.enums.TipoCliente;
import com.desafio.estagio.repository.ClienteFisicoRepository;
import com.desafio.estagio.repository.ClienteJuridicoRepository;
import com.desafio.estagio.service.JasperReportService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class JasperReportServiceImpl implements JasperReportService {

    private static final int DEFAULT_PAGE_SIZE = 1000;

    private final Map<String, JasperReport> jasperReports;
    private final ClienteFisicoRepository fisicoRepository;
    private final ClienteJuridicoRepository juridicoRepository;

    public JasperReportServiceImpl(
            Map<String, JasperReport> jasperReports,
            ClienteFisicoRepository fisicoRepository,
            ClienteJuridicoRepository juridicoRepository) {

        this.jasperReports = jasperReports;
        this.fisicoRepository = fisicoRepository;
        this.juridicoRepository = juridicoRepository;
    }

    // =====================================================
    // TYPE-SPECIFIC GENERATION
    // =====================================================

    @Override
    public byte[] generateForFisicos(String reportName, Map<String, Object> parameters) {
        log.debug("Generating report '{}' for all physical clients", reportName);

        List<ClienteFisicoDTO.ReportResponse> data = fisicoRepository.findAll().stream()
                .map(this::toFisicoReportResponse)
                .collect(Collectors.toList());

        return generatePdfWithClientType(reportName, data, parameters, TipoCliente.FISICA);
    }

    @Override
    public byte[] generateForJuridicos(String reportName, Map<String, Object> parameters) {
        log.debug("Generating report '{}' for all legal clients", reportName);

        List<ClienteJuridicoDTO.ReportResponse> data = juridicoRepository.findAll().stream()
                .map(this::toJuridicoReportResponse)
                .collect(Collectors.toList());

        return generatePdfWithClientType(reportName, data, parameters, TipoCliente.JURIDICA);
    }

    @Override
    public byte[] generateForClientes(String reportName, Map<String, Object> parameters, TipoCliente type) {
        log.debug("Generating report '{}' for client type: {}", reportName, type);

        if (type == TipoCliente.FISICA) {
            return generateForFisicos(reportName, parameters);
        } else if (type == TipoCliente.JURIDICA) {
            return generateForJuridicos(reportName, parameters);
        }

        throw new BusinessException("Tipo de cliente não suportado: " + type);
    }

    // =====================================================
    // PAGINATED GENERATION (MEMORY SAFE)
    // =====================================================

    @Override
    public byte[] generateForFisicos(String reportName, Map<String, Object> parameters, Pageable pageable) {
        log.debug("Generating report '{}' for physical clients with pagination: page={}, size={}",
                reportName, pageable.getPageNumber(), pageable.getPageSize());

        Page<ClienteFisicoDTO.ReportResponse> page = fisicoRepository.findAll(pageable)
                .map(this::toFisicoReportResponse);

        return generatePdfWithPagination(reportName, page, parameters, TipoCliente.FISICA);
    }

    @Override
    public byte[] generateForJuridicos(String reportName, Map<String, Object> parameters, Pageable pageable) {
        log.debug("Generating report '{}' for legal clients with pagination: page={}, size={}",
                reportName, pageable.getPageNumber(), pageable.getPageSize());

        Page<ClienteJuridicoDTO.ReportResponse> page = juridicoRepository.findAll(pageable)
                .map(this::toJuridicoReportResponse);

        return generatePdfWithPagination(reportName, page, parameters, TipoCliente.JURIDICA);
    }

    // =====================================================
    // GENERIC GENERATION METHODS
    // =====================================================

    @Override
    public byte[] generatePdf(String reportName, List<?> data, Map<String, Object> parameters) {
        log.debug("Generating PDF '{}' with {} records", reportName, data != null ? data.size() : 0);

        try {
            JasperReport jasperReport = getJasperReport(reportName);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data != null ? data : Collections.emptyList());

            Map<String, Object> mergedParams = new HashMap<>();
            if (parameters != null) {
                mergedParams.putAll(parameters);
            }
            mergedParams.put("TABLE_DATA_SOURCE", dataSource);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, mergedParams, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            log.error("Failed to generate PDF report: {}", reportName, e);
            throw new BusinessException("Erro ao gerar relatório PDF: " + e.getMessage());
        }
    }

    @Override
    public byte[] generatePdfWithPagination(String reportName, Page<?> page, Map<String, Object> parameters) {
        log.debug("Generating PDF '{}' with pagination - page {}/{}",
                reportName, page.getNumber(), page.getTotalPages());

        Map<String, Object> mergedParams = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
        mergedParams.put("PAGE_NUMBER", page.getNumber() + 1);
        mergedParams.put("TOTAL_PAGES", page.getTotalPages());
        mergedParams.put("TOTAL_ELEMENTS", page.getTotalElements());
        mergedParams.put("PAGE_SIZE", page.getSize());
        mergedParams.put("HAS_NEXT", page.hasNext());
        mergedParams.put("HAS_PREVIOUS", page.hasPrevious());

        return generatePdf(reportName, page.getContent(), mergedParams);
    }

    @Override
    public byte[] generatePdfFromObject(String reportName, Object data, Map<String, Object> parameters) {
        log.debug("Generating PDF '{}' from single object", reportName);
        return generatePdf(reportName, data != null ? List.of(data) : Collections.emptyList(), parameters);
    }

    @Override
    public byte[] generatePdfWithFields(String reportName, Map<String, Object> parameters) {
        log.debug("Generating PDF '{}' with parameters only", reportName);

        try {
            JasperReport jasperReport = getJasperReport(reportName);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            log.error("Failed to generate parameter-only PDF report: {}", reportName, e);
            throw new BusinessException("Erro ao gerar relatório PDF: " + e.getMessage());
        }
    }

    @Override
    public byte[] generatePdfWithDataSource(String reportName, JRDataSource dataSource, Map<String, Object> parameters) {
        log.debug("Generating PDF '{}' with custom data source", reportName);

        try {
            JasperReport jasperReport = getJasperReport(reportName);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            log.error("Failed to generate PDF with data source: {}", reportName, e);
            throw new BusinessException("Erro ao gerar relatório PDF: " + e.getMessage());
        }
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    @Override
    public JasperReport getJasperReport(String reportName) {
        JasperReport jasperReport = jasperReports.get(reportName);
        if (jasperReport == null) {
            String availableReports = String.join(", ", jasperReports.keySet());
            log.error("Report not found: {}. Available: {}", reportName, availableReports);
            throw new BusinessException(String.format(
                    "Relatório '%s' não encontrado. Relatórios disponíveis: %s",
                    reportName, availableReports));
        }
        return jasperReport;
    }

    @Override
    public boolean reportExists(String reportName) {
        boolean exists = jasperReports.containsKey(reportName);
        log.debug("Report '{}' exists: {}", reportName, exists);
        return exists;
    }

    @Override
    public List<String> getAvailableReports() {
        return new ArrayList<>(jasperReports.keySet());
    }

    // =====================================================
    // PRIVATE HELPER METHODS
    // =====================================================

    private byte[] generatePdfWithClientType(String reportName, List<?> data, Map<String, Object> parameters, TipoCliente type) {
        Map<String, Object> mergedParams = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
        mergedParams.put("TIPO_CLIENTE", type.name());
        mergedParams.put("DATA_EMISSAO", new Date());
        mergedParams.put("TOTAL_REGISTROS", data.size());

        return generatePdf(reportName, data, mergedParams);
    }

    private byte[] generatePdfWithPagination(String reportName, Page<?> page, Map<String, Object> parameters, TipoCliente type) {
        Map<String, Object> mergedParams = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
        mergedParams.put("TIPO_CLIENTE", type.name());
        mergedParams.put("DATA_EMISSAO", new Date());

        return generatePdfWithPagination(reportName, page, mergedParams);
    }

    // =====================================================
    // MAPPING METHODS (can be replaced with MapStruct later)
    // =====================================================

    private ClienteFisicoDTO.ReportResponse toFisicoReportResponse(ClienteFisico entity) {
        if (entity == null) return null;

        return ClienteFisicoDTO.ReportResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .cpf(entity.getCpf())
                .rg(entity.getRg())
                .email(entity.getEmail())
                .dataNascimento(entity.getDataNascimento())
                .estaAtivo(entity.getEstaAtivo())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toLocalDate() : null)
                .build();
    }

    private ClienteJuridicoDTO.ReportResponse toJuridicoReportResponse(ClienteJuridico entity) {
        if (entity == null) return null;

        return ClienteJuridicoDTO.ReportResponse.builder()
                .id(entity.getId())
                .razaoSocial(entity.getRazaoSocial())
                .cnpj(entity.getCnpj())
                .inscricaoEstadual(entity.getInscricaoEstadual())
                .email(entity.getEmail())
                .dataCriacaoEmpresa(entity.getDataCriacaoEmpresa())
                .estaAtivo(entity.getEstaAtivo())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : null)
                .build();
    }

    // =====================================================
    // OPTIONAL: STREAMING FOR VERY LARGE DATASETS
    // =====================================================

    /**
     * Generates report using streaming for very large datasets (>10,000 records)
     * This method loads data in chunks to avoid memory issues
     */
    public byte[] generateWithStreaming(String reportName, TipoCliente type, Map<String, Object> parameters) {
        log.debug("Generating report '{}' with streaming for type: {}", reportName, type);

        List<Object> allData = new ArrayList<>();
        int pageNumber = 0;
        Pageable pageable = PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE);
        Page<?> page;

        do {
            if (type == TipoCliente.FISICA) {
                page = fisicoRepository.findAll(pageable).map(this::toFisicoReportResponse);
            } else {
                page = juridicoRepository.findAll(pageable).map(this::toJuridicoReportResponse);
            }

            allData.addAll(page.getContent());
            pageNumber++;
            pageable = PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE);

            log.debug("Loaded page {} with {} records. Total: {}",
                    pageNumber, page.getNumberOfElements(), allData.size());

        } while (page.hasNext());

        log.info("Streaming complete. Loaded {} records for report: {}", allData.size(), reportName);

        return generatePdfWithClientType(reportName, allData, parameters, type);
    }
}