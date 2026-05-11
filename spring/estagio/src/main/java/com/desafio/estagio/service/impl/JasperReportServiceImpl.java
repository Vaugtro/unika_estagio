package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.ClienteFisicoDTO;
import com.desafio.estagio.dto.ClienteJuridicoDTO;
import com.desafio.estagio.model.Cliente;
import com.desafio.estagio.model.enums.TipoCliente;
import com.desafio.estagio.repository.ClienteFisicoRepository;
import com.desafio.estagio.repository.ClienteJuridicoRepository;
import com.desafio.estagio.repository.ClienteRepository;
import com.desafio.estagio.service.JasperReportService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class JasperReportServiceImpl implements JasperReportService {

    private static final Map<TipoCliente, Class<?>> DTO_TYPE_MAP = new HashMap<>();

    static {
        DTO_TYPE_MAP.put(TipoCliente.FISICA, ClienteFisicoDTO.ReportResponse.class);
        DTO_TYPE_MAP.put(TipoCliente.JURIDICA, ClienteJuridicoDTO.ReportResponse.class);
    }

    private final Map<String, JasperReport> jasperReports;
    private final Map<TipoCliente, ClienteRepository<? extends Cliente>> repositoryMap;
    private final DataSource dataSource;

    // EntityManager não é mais necessário (removido)
    public JasperReportServiceImpl(
            Map<String, JasperReport> jasperReports,
            ClienteFisicoRepository fisicoRepository,
            ClienteJuridicoRepository juridicoRepository, DataSource dataSource) {
        this.jasperReports = jasperReports;
        this.repositoryMap = new HashMap<>();
        this.repositoryMap.put(TipoCliente.FISICA, fisicoRepository);
        this.repositoryMap.put(TipoCliente.JURIDICA, juridicoRepository);
        this.dataSource = dataSource;
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

        List<?> clientes = repository.findAll();  // Declare outside the if/else blocks

        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put("TIPO_CLIENTE", type.name());

        return generatePdf(reportName, clientes, parameters);
    }


    @Override
    public byte[] generatePdf(String reportName, List<?> data, Map<String, Object> parameters) {
        try {
            JasperReport jasperReport = jasperReports.get(reportName);

            if (jasperReport == null) {
                throw new RuntimeException("Report not found: " + reportName +
                        ". Available: " + String.join(", ", jasperReports.keySet()));
            }

            // Create data source from collection
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

            Map<String, Object> mergedParams = new HashMap<>();
            if (parameters != null) {
                mergedParams.putAll(parameters);
                mergedParams.put("TABLE_DATA_SOURCE", dataSource);
            }

            // Fill report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, mergedParams, new JREmptyDataSource());

            // Export to PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            throw new RuntimeException("Failed to generate PDF report: " + reportName, e);
        }
    }

    @Override
    public byte[] generatePdfFromObject(String reportName, Object data, Map<String, Object> parameters) {
        return generatePdf(reportName, List.of(data), parameters);
    }

    @Override
    public byte[] generatePdfWithFields(String reportName, Map<String, Object> parameters) {
        try {
            JasperReport jasperReport = jasperReports.get(reportName);

            if (jasperReport == null) {
                throw new RuntimeException("Report not found: " + reportName);
            }

            // Empty data source for reports without data
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            throw new RuntimeException("Failed to generate PDF report: " + reportName, e);
        }
    }

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

    @Override
    public JasperReport getJasperReport(String reportName) {
        JasperReport jasperReport = jasperReports.get(reportName);
        if (jasperReport == null) {
            throw new RuntimeException("Report not found: " + reportName +
                    ". Available reports: " + String.join(", ", jasperReports.keySet()));
        }
        return jasperReport;
    }

    @Override
    public boolean reportExists(String reportName) {
        return jasperReports.containsKey(reportName);
    }

    @Override
    public List<String> getAvailableReports() {
        return new ArrayList<>(jasperReports.keySet());
    }
}