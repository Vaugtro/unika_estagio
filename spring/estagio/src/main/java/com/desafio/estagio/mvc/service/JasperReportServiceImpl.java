package com.desafio.estagio.mvc.service;

import com.desafio.estagio.mvc.model.dto.TipoCliente;
import com.desafio.estagio.mvc.model.entity.Cliente;
import com.desafio.estagio.repository.ClienteFisicoRepository;
import com.desafio.estagio.repository.ClienteJuridicoRepository;
import com.desafio.estagio.repository.ClienteRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class JasperReportServiceImpl implements JasperReportService {

    private final Map<String, JasperReport> jasperReports;

    private final Map<TipoCliente, ClienteRepository<? extends Cliente>> repositoryMap;

    public JasperReportServiceImpl(
            Map<String, JasperReport> jasperReports,
            ClienteFisicoRepository fisicoRepository,
            ClienteJuridicoRepository juridicoRepository) {
        this.jasperReports = jasperReports;
        this.repositoryMap = new HashMap<>();
        this.repositoryMap.put(TipoCliente.FISICA, fisicoRepository);
        this.repositoryMap.put(TipoCliente.JURIDICA, juridicoRepository);
    }

    @Override
    public byte[] generateForCliente(
            String reportName,
            Map<String, Object> parameters,
            TipoCliente type) {

        ClienteRepository<?> repository = repositoryMap.get(type);

        if (repository == null) {
            throw new IllegalArgumentException("No repository found for type: " + type);
        }

        List<?> clientData = repository.findAll();

        return generatePdf(reportName, clientData, parameters);
    }


    /**
     * Generate PDF report from a collection of data
     */
    @Override
    public byte[] generatePdf(String reportName, Collection<?> data, Map<String, Object> parameters) {
        try {
            JasperReport jasperReport = jasperReports.get(reportName);

            if (jasperReport == null) {
                throw new RuntimeException("Report not found: " + reportName);
            }

            // Create data source
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

            // Merge parameters
            if (parameters == null) {
                parameters = new HashMap<>();
            }
            parameters.putIfAbsent("REPORT_TITLE", reportName);
            parameters.putIfAbsent("GENERATED_DATE", new Date());

            // Fill report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Export to PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            throw new RuntimeException("Failed to generate PDF report: " + reportName, e);
        }
    }

    /**
     * Generate PDF report from a single object
     */
    @Override
    public byte[] generatePdfFromObject(String reportName, Object data, Map<String, Object> parameters) {
        return generatePdf(reportName, List.of(data), parameters);
    }

    /**
     * Generate PDF report with custom fields
     */
    @Override
    public byte[] generatePdfWithFields(String reportName, Map<String, Object> parameters) {
        try {
            JasperReport jasperReport = jasperReports.get(reportName);

            if (jasperReport == null) {
                throw new RuntimeException("Report not found: " + reportName);
            }

            // Fill report without data source (for reports with subreports)
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            throw new RuntimeException("Failed to generate PDF report: " + reportName, e);
        }
    }

    /**
     * Generate PDF report with a custom data source
     */
    @Override
    public byte[] generatePdfWithDataSource(String reportName, JRDataSource dataSource, Map<String, Object> parameters) {
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
     */
    @Override
    public JasperReport getJasperReport(String reportName) {
        JasperReport jasperReport = jasperReports.get(reportName);
        if (jasperReport == null) {
            throw new RuntimeException("Report not found: " + reportName +
                    ". Available reports: " + String.join(", ", jasperReports.keySet()));
        }
        return jasperReport;
    }

    /**
     * Check if a report exists
     */
    @Override
    public boolean reportExists(String reportName) {
        return jasperReports.containsKey(reportName);
    }

    /**
     * Get all available report names
     */
    @Override
    public List<String> getAvailableReports() {
        return new ArrayList<>(jasperReports.keySet());
    }
}