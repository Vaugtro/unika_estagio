package com.desafio.estagio.service;

import com.desafio.estagio.model.enums.TipoCliente;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface JasperReportService {

    // Report generation by client type
    byte[] generateForFisicos(String reportName, Map<String, Object> parameters);

    byte[] generateForJuridicos(String reportName, Map<String, Object> parameters);

    byte[] generateForClientes(String reportName, Map<String, Object> parameters, TipoCliente type);

    // Report generation with pagination (memory safe)
    byte[] generateForFisicos(String reportName, Map<String, Object> parameters, Pageable pageable);

    byte[] generateForJuridicos(String reportName, Map<String, Object> parameters, Pageable pageable);

    // Generic generation methods
    byte[] generatePdf(String reportName, List<?> data, Map<String, Object> parameters);

    byte[] generatePdfWithPagination(String reportName, Page<?> page, Map<String, Object> parameters);

    byte[] generatePdfFromObject(String reportName, Object data, Map<String, Object> parameters);

    byte[] generatePdfWithFields(String reportName, Map<String, Object> parameters);

    byte[] generatePdfWithDataSource(String reportName, JRDataSource dataSource, Map<String, Object> parameters);

    // Utility methods
    JasperReport getJasperReport(String reportName);

    boolean reportExists(String reportName);

    List<String> getAvailableReports();
}