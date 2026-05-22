package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoCreateRequest;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoReportResponse;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoCreateRequest;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoReportResponse;
import com.desafio.estagio.dto.endereco.EnderecoCreateRequest;
import com.desafio.estagio.dto.endereco.EnderecoResponse;
import com.desafio.estagio.dto.endereco.EnderecoWithinClienteCreateRequest;
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
import java.io.InputStream;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
    // IMPORT TEMPLATES (blank XLSX with headers only)
    // =====================================================================

    @Override
    public byte[] templateFisicosImport() {
        String[] headers = {"CPF", "Nome", "RG", "Email", "Data de Nascimento",
                "Logradouro", "Número", "CEP", "Bairro", "Telefone", "Estado", "Cidade",
                "Principal", "Complemento"};
        return generateTemplate(headers, "Import Clientes Fisicos");
    }

    @Override
    public byte[] templateJuridicosImport() {
        String[] headers = {"CNPJ", "Razão Social", "Inscrição Estadual", "Email", "Data de Criação",
                "Logradouro", "Número", "CEP", "Bairro", "Telefone", "Estado", "Cidade",
                "Principal", "Complemento"};
        return generateTemplate(headers, "Import Clientes Juridicos");
    }

    @Override
    public byte[] templateEnderecosImport() {
        String[] headers = {"Logradouro", "Número", "CEP", "Bairro", "Telefone", "Estado", "Cidade",
                "Principal", "Complemento"};
        return generateTemplate(headers, "Import Enderecos");
    }

    private byte[] generateTemplate(String[] headers, String sheetName) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            String safeName = sheetName.length() > 31 ? sheetName.substring(0, 31) : sheetName;
            Sheet sheet = wb.createSheet(safeName);

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

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar template: " + e.getMessage(), e);
        }
    }

    // =====================================================================
    // XLSX IMPORT
    // =====================================================================

    @Override
    @Transactional
    public int importFisicos(InputStream xlsx) {
        List<String> errors = new ArrayList<>();
        int count = 0;

        try (XSSFWorkbook wb = new XSSFWorkbook(xlsx)) {
            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (isRowEmpty(row)) continue;

                try {
                    String cpf = getCellString(row, 0);
                    String nome = getCellString(row, 1);
                    String rg = getCellString(row, 2);
                    String email = getCellString(row, 3);
                    LocalDate dataNascimento = parseDate(getCellString(row, 4));

                    String logradouro = getCellString(row, 5);
                    Long numero = getCellLong(row, 6);
                    String cep = getCellString(row, 7);
                    String bairro = getCellString(row, 8);
                    String telefone = getCellString(row, 9);
                    String estado = getCellString(row, 10);
                    String cidade = getCellString(row, 11);
                    Boolean principal = getCellBoolean(row, 12);
                    String complemento = getCellString(row, 13);

                    var endereco = EnderecoWithinClienteCreateRequest.builder()
                            .logradouro(logradouro)
                            .numero(numero)
                            .cep(cep)
                            .bairro(bairro)
                            .telefone(telefone)
                            .estado(estado)
                            .cidade(cidade)
                            .principal(principal)
                            .complemento(complemento)
                            .build();

                    var request = ClienteFisicoCreateRequest.builder()
                            .cpf(cpf)
                            .nome(nome)
                            .rg(rg)
                            .email(email)
                            .dataNascimento(dataNascimento)
                            .enderecos(List.of(endereco))
                            .build();

                    clienteFisicoService.create(request);
                    count++;
                } catch (Exception e) {
                    errors.add("Linha " + (row.getRowNum() + 1) + ": " + e.getMessage());
                    log.warn("Erro ao importar linha {}: {}", row.getRowNum() + 1, e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler arquivo XLSX: " + e.getMessage(), e);
        }

        if (!errors.isEmpty()) {
            log.warn("Importação concluída com {} sucessos e {} erros: {}",
                    count, errors.size(), String.join(" | ", errors));
        }
        return count;
    }

    @Override
    @Transactional
    public int importJuridicos(InputStream xlsx) {
        List<String> errors = new ArrayList<>();
        int count = 0;

        try (XSSFWorkbook wb = new XSSFWorkbook(xlsx)) {
            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (isRowEmpty(row)) continue;

                try {
                    String cnpj = getCellString(row, 0);
                    String razaoSocial = getCellString(row, 1);
                    String inscricaoEstadual = getCellString(row, 2);
                    String email = getCellString(row, 3);
                    LocalDate dataCriacaoEmpresa = parseDate(getCellString(row, 4));

                    String logradouro = getCellString(row, 5);
                    Long numero = getCellLong(row, 6);
                    String cep = getCellString(row, 7);
                    String bairro = getCellString(row, 8);
                    String telefone = getCellString(row, 9);
                    String estado = getCellString(row, 10);
                    String cidade = getCellString(row, 11);
                    Boolean principal = getCellBoolean(row, 12);
                    String complemento = getCellString(row, 13);

                    var endereco = EnderecoCreateRequest.builder()
                            .logradouro(logradouro)
                            .numero(numero)
                            .cep(cep)
                            .bairro(bairro)
                            .telefone(telefone)
                            .estado(estado)
                            .cidade(cidade)
                            .principal(principal)
                            .complemento(complemento)
                            .clienteId(null)
                            .build();

                    var request = ClienteJuridicoCreateRequest.builder()
                            .cnpj(cnpj)
                            .razaoSocial(razaoSocial)
                            .inscricaoEstadual(inscricaoEstadual)
                            .email(email)
                            .dataCriacaoEmpresa(dataCriacaoEmpresa)
                            .enderecos(List.of(endereco))
                            .build();

                    clienteJuridicoService.create(request);
                    count++;
                } catch (Exception e) {
                    errors.add("Linha " + (row.getRowNum() + 1) + ": " + e.getMessage());
                    log.warn("Erro ao importar linha {}: {}", row.getRowNum() + 1, e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler arquivo XLSX: " + e.getMessage(), e);
        }

        if (!errors.isEmpty()) {
            log.warn("Importação concluída com {} sucessos e {} erros: {}",
                    count, errors.size(), String.join(" | ", errors));
        }
        return count;
    }

    @Override
    @Transactional
    public int importEnderecos(Long clienteId, InputStream xlsx) {
        List<String> errors = new ArrayList<>();
        int count = 0;

        try (XSSFWorkbook wb = new XSSFWorkbook(xlsx)) {
            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (isRowEmpty(row)) continue;

                try {
                    String logradouro = getCellString(row, 0);
                    Long numero = getCellLong(row, 1);
                    String cep = getCellString(row, 2);
                    String bairro = getCellString(row, 3);
                    String telefone = getCellString(row, 4);
                    String estado = getCellString(row, 5);
                    String cidade = getCellString(row, 6);
                    Boolean principal = getCellBoolean(row, 7);
                    String complemento = getCellString(row, 8);

                    var request = EnderecoCreateRequest.builder()
                            .logradouro(logradouro)
                            .numero(numero)
                            .cep(cep)
                            .bairro(bairro)
                            .telefone(telefone)
                            .estado(estado)
                            .cidade(cidade)
                            .principal(principal)
                            .complemento(complemento)
                            .clienteId(clienteId)
                            .build();

                    enderecoService.create(request);
                    count++;
                } catch (Exception e) {
                    errors.add("Linha " + (row.getRowNum() + 1) + ": " + e.getMessage());
                    log.warn("Erro ao importar linha {}: {}", row.getRowNum() + 1, e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler arquivo XLSX: " + e.getMessage(), e);
        }

        if (!errors.isEmpty()) {
            log.warn("Importação concluída com {} sucessos e {} erros: {}",
                    count, errors.size(), String.join(" | ", errors));
        }
        return count;
    }

    // =====================================================================
    // XLSX cell helpers and date parsing
    // =====================================================================

    private String getCellString(Row row, int idx) {
        Cell cell = row.getCell(idx);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    yield String.valueOf((long) val);
                }
                yield String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private Long getCellLong(Row row, int idx) {
        Cell cell = row.getCell(idx);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> (long) cell.getNumericCellValue();
            case STRING -> {
                String val = cell.getStringCellValue().trim();
                if (val.isEmpty()) yield null;
                try {
                    yield Long.parseLong(val.replaceAll("\\D", ""));
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }

    private Boolean getCellBoolean(Row row, int idx) {
        Cell cell = row.getCell(idx);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case BOOLEAN -> cell.getBooleanCellValue();
            case STRING -> {
                String val = cell.getStringCellValue().trim().toLowerCase();
                yield "sim".equals(val) || "true".equals(val) || "s".equals(val) || "1".equals(val);
            }
            case NUMERIC -> cell.getNumericCellValue() == 1;
            default -> null;
        };
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        value = value.trim();

        // Try ISO format (yyyy-MM-dd)
        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
        }

        // Try Brazilian format (dd/MM/yyyy)
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException ignored) {
        }

        // Try dd-MM-yyyy
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException ignored) {
        }

        // Try yyyy/MM/dd
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        } catch (DateTimeParseException ignored) {
        }

        log.warn("Could not parse date: {}", value);
        return null;
    }

    private boolean isRowEmpty(Row row) {
        for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellString(row, i).isEmpty()) {
                return false;
            }
        }
        return true;
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
