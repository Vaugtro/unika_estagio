package com.desafio.estagio.mvc.service;

import com.desafio.estagio.mvc.model.dto.TipoCliente;
import net.sf.jasperreports.engine.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Service interface for generating JasperReports
 */
public interface JasperReportService {

    public byte[] generateForCliente(
            String reportName,
            Map<String, Object> parameters,
            TipoCliente type);

    /**
     * Generate PDF report from a collection of data
     *
     * @param reportName Name of the report (matches the .jrxml filename without extension)
     * @param data Collection of data objects to be used in the report
     * @param parameters Additional parameters to pass to the report
     * @return byte array containing the PDF report
     * @throws RuntimeException if report not found or generation fails
     */
    byte[] generatePdf(String reportName, List<?> data, Map<String, Object> parameters);

    /**
     * Generate PDF report from a single object
     *
     * @param reportName Name of the report (matches the .jrxml filename without extension)
     * @param data Single data object to be used in the report
     * @param parameters Additional parameters to pass to the report
     * @return byte array containing the PDF report
     * @throws RuntimeException if report not found or generation fails
     */
    byte[] generatePdfFromObject(String reportName, Object data, Map<String, Object> parameters);

    /**
     * Generate PDF report with custom fields only (no data source)
     * Useful for reports that don't require a data source or use subreports
     *
     * @param reportName Name of the report (matches the .jrxml filename without extension)
     * @param parameters Additional parameters to pass to the report
     * @return byte array containing the PDF report
     * @throws RuntimeException if report not found or generation fails
     */
    byte[] generatePdfWithFields(String reportName, Map<String, Object> parameters);

    /**
     * Generate PDF report with a custom data source
     *
     * @param reportName Name of the report (matches the .jrxml filename without extension)
     * @param dataSource Custom JRDataSource implementation
     * @param parameters Additional parameters to pass to the report
     * @return byte array containing the PDF report
     * @throws RuntimeException if report not found or generation fails
     */
    default byte[] generatePdfWithDataSource(String reportName, JRDataSource dataSource, Map<String, Object> parameters) {
        try {
            JasperReport jasperReport = getJasperReport(reportName);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            throw new RuntimeException("Failed to generate PDF report: " + reportName, e);
        }
    }

    /**
     * Get the compiled JasperReport object by name
     *
     * @param reportName Name of the report
     * @return Compiled JasperReport object
     * @throws RuntimeException if report not found
     */
    JasperReport getJasperReport(String reportName);

    /**
     * Check if a report exists
     *
     * @param reportName Name of the report
     * @return true if report exists, false otherwise
     */
    boolean reportExists(String reportName);

    /**
     * Get all available report names
     *
     * @return List of report names
     */
    List<String> getAvailableReports();
}