package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoReportResponse;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoReportResponse;
import com.desafio.estagio.dto.endereco.EnderecoResponse;
import com.desafio.estagio.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExportServiceImpl implements ExportService {

    private final ClienteFisicoService clienteFisicoService;
    private final ClienteJuridicoService clienteJuridicoService;
    private final EnderecoService enderecoService;
    private final JasperReportService jasperReportService;

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
        // TABLE_DATA_SOURCE must be a SEPARATE instance from the main data source
        // because the main report consumes it during fillReport iteration,
        // and the table sub-dataset needs its own independent iteration.
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
    // XLSX — Apache POI
    // =====================================================================

    @Override
    public byte[] xlsxFisicos() {
        List<ClienteFisicoReportResponse> data = clienteFisicoService
                .findAllForReport(PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();

        String[] headers = {"ID", "Nome", "CPF", "RG", "Email", "Data Nasc.", "Ativo", "Criado em"};
        String[] fields = {"id", "nome", "cpf", "rg", "email", "dataNascimento", "estaAtivo", "createdAt"};
        return generateXlsx(data, headers, fields, "Clientes Fisicos");
    }

    @Override
    public byte[] xlsxJuridicos() {
        List<ClienteJuridicoReportResponse> data = clienteJuridicoService
                .findAllForReport(PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();

        String[] headers = {"ID", "Razao Social", "CNPJ", "Insc. Estadual", "Email", "Ativo", "Dt. Criacao Emp.", "Criado em"};
        String[] fields = {"id", "razaoSocial", "cnpj", "inscricaoEstadual", "email", "estaAtivo", "dataCriacaoEmpresa", "createdAt"};
        return generateXlsx(data, headers, fields, "Clientes Juridicos");
    }

    @Override
    public byte[] xlsxEnderecos(Long clienteId) {
        List<EnderecoResponse> data = enderecoService.findAllByClienteId(clienteId);

        String[] headers = {"Logradouro", "Numero", "CEP", "Bairro", "Cidade", "UF", "Telefone", "Principal"};
        String[] fields = {"logradouro", "numero", "cep", "bairro", "cidade", "estado", "telefone", "principal"};
        return generateXlsx(data, headers, fields, "Enderecos");
    }

    // =====================================================================
    // Internal XLSX generator
    // =====================================================================

    private byte[] generateXlsx(List<?> data, String[] headers, String[] fields, String sheetName) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            String safeName = sheetName.length() > 31 ? sheetName.substring(0, 31) : sheetName;
            Sheet sheet = wb.createSheet(safeName);

            // Header style
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Data style
            CellStyle dataStyle = wb.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowIdx = 1;
            for (Object bean : data) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < fields.length; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellStyle(dataStyle);
                    Object val = readProperty(bean, fields[i]);
                    cell.setCellValue(val != null ? val.toString() : "");
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar XLSX: " + e.getMessage(), e);
        }
    }

    private Object readProperty(Object bean, String fieldName) {
        if (bean == null) return null;
        try {
            String capitalized = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            for (String prefix : new String[]{"get", "is"}) {
                try {
                    Method m = bean.getClass().getMethod(prefix + capitalized);
                    return m.invoke(bean);
                } catch (NoSuchMethodException ignored) {
                    // try next
                }
            }
            // Java record accessor (no prefix)
            try {
                Method m = bean.getClass().getMethod(fieldName);
                return m.invoke(bean);
            } catch (NoSuchMethodException ignored) {
            }

            log.warn("No accessor for field '{}' on {}", fieldName, bean.getClass().getSimpleName());
            return null;
        } catch (Exception e) {
            log.warn("Error reading property '{}': {}", fieldName, e.getMessage());
            return null;
        }
    }
}
