package com.desafio.estagio.service.impl;

import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.model.enums.TipoCliente;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.service.ClienteJuridicoService;
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
import java.util.function.Function;

@Slf4j
@Service
@Transactional(readOnly = true)
public class JasperReportServiceImpl implements JasperReportService {

    private static final int DEFAULT_PAGE_SIZE = 1000;

    private final Map<String, JasperReport> jasperReports;
    private final ClienteFisicoService fisicoService;
    private final ClienteJuridicoService juridicoService;
    private final Map<TipoCliente, Function<Pageable, Page<?>>> reportDataProviders;

    public JasperReportServiceImpl(
            Map<String, JasperReport> jasperReports,
            ClienteFisicoService fisicoService,
            ClienteJuridicoService juridicoService) {

        this.jasperReports = jasperReports;
        this.fisicoService = fisicoService;
        this.juridicoService = juridicoService;

        this.reportDataProviders = new EnumMap<>(TipoCliente.class);
        this.reportDataProviders.put(TipoCliente.FISICA, pageable -> fisicoService.findAllForReport(pageable));
        this.reportDataProviders.put(TipoCliente.JURIDICA, pageable -> juridicoService.findAllForReport(pageable));
    }

    // =====================================================
    // TYPE-SPECIFIC GENERATION
    // =====================================================

    @Override
    public byte[] generateForFisicos(String reportName, Map<String, Object> parameters) {
        return generateForClientes(reportName, parameters, TipoCliente.FISICA);
    }

    @Override
    public byte[] generateForJuridicos(String reportName, Map<String, Object> parameters) {
        return generateForClientes(reportName, parameters, TipoCliente.JURIDICA);
    }

    @Override
    public byte[] generateForClientes(String reportName, Map<String, Object> parameters, TipoCliente type) {
        log.debug("Generating report '{}' for client type: {}", reportName, type);

        Function<Pageable, Page<?>> provider = reportDataProviders.get(type);
        if (provider == null) {
            throw new BusinessException("Tipo de cliente não suportado: " + type);
        }

        // Load all data via service layer (which uses MapStruct mappers)
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<?> page = provider.apply(pageable);
        List<?> data = page.getContent();

        return generatePdfWithClientType(reportName, data, parameters, type);
    }

    // =====================================================
    // PAGINATED GENERATION (MEMORY SAFE)
    // =====================================================

    @Override
    public byte[] generateForFisicos(String reportName, Map<String, Object> parameters, Pageable pageable) {
        log.debug("Generating report '{}' for physical clients with pagination: page={}, size={}",
                reportName, pageable.getPageNumber(), pageable.getPageSize());

        Page<?> page = fisicoService.findAllForReport(pageable);
        return generatePdfWithPagination(reportName, page, parameters, TipoCliente.FISICA);
    }

    @Override
    public byte[] generateForJuridicos(String reportName, Map<String, Object> parameters, Pageable pageable) {
        log.debug("Generating report '{}' for legal clients with pagination: page={}, size={}",
                reportName, pageable.getPageNumber(), pageable.getPageSize());

        Page<?> page = juridicoService.findAllForReport(pageable);
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
    // OPTIONAL: STREAMING FOR VERY LARGE DATASETS
    // =====================================================

    /**
     * Generates report using true incremental streaming for very large datasets (>10,000 records).
     * Uses JRDataSource to feed data to JasperReports page-by-page, avoiding loading all records into memory.
     */
    public byte[] generateWithStreaming(String reportName, TipoCliente type, Map<String, Object> parameters) {
        log.debug("Generating report '{}' with streaming for type: {}", reportName, type);

        Function<Pageable, Page<?>> provider = reportDataProviders.get(type);
        if (provider == null) {
            throw new BusinessException("Tipo de cliente não suportado: " + type);
        }

        // Use a paginated JRDataSource that loads chunks on demand
        JRDataSource streamingDataSource = new PaginatedJRDataSource(provider, DEFAULT_PAGE_SIZE);

        Map<String, Object> mergedParams = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
        mergedParams.put("TIPO_CLIENTE", type.name());
        mergedParams.put("DATA_EMISSAO", new Date());

        return generatePdfWithDataSource(reportName, streamingDataSource, mergedParams);
    }

    /**
     * A JRDataSource that loads data in pages on demand, keeping only one page in memory at a time.
     */
    private static class PaginatedJRDataSource implements JRDataSource {

        private final Function<Pageable, Page<?>> provider;
        private final int pageSize;
        private int currentPageNumber = 0;
        private List<?> currentPageData = Collections.emptyList();
        private int indexInPage = -1;
        private boolean exhausted = false;
        private Object currentBean;

        PaginatedJRDataSource(Function<Pageable, Page<?>> provider, int pageSize) {
            this.provider = provider;
            this.pageSize = pageSize;
        }

        @Override
        public boolean next() {
            indexInPage++;
            if (indexInPage < currentPageData.size()) {
                currentBean = currentPageData.get(indexInPage);
                return true;
            }
            if (exhausted) {
                return false;
            }
            // Load next page
            Page<?> page = provider.apply(PageRequest.of(currentPageNumber, pageSize));
            currentPageData = page.getContent();
            currentPageNumber++;
            exhausted = !page.hasNext();
            indexInPage = 0;
            if (currentPageData.isEmpty()) {
                return false;
            }
            currentBean = currentPageData.get(0);
            return true;
        }

        @Override
        public Object getFieldValue(JRField jrField) throws JRException {
            if (currentBean == null) {
                return null;
            }
            try {
                var field = currentBean.getClass().getDeclaredField(jrField.getName());
                field.setAccessible(true);
                return field.get(currentBean);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Try getter method
                try {
                    String getterName = "get" + Character.toUpperCase(jrField.getName().charAt(0)) + jrField.getName().substring(1);
                    var method = currentBean.getClass().getMethod(getterName);
                    return method.invoke(currentBean);
                } catch (Exception ex) {
                    throw new JRException("Cannot get field value: " + jrField.getName(), ex);
                }
            }
        }
    }
}