package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoCreateRequest;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoReportResponse;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoCreateRequest;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoReportResponse;
import com.desafio.estagio.dto.endereco.EnderecoCreateRequest;
import com.desafio.estagio.dto.endereco.EnderecoResponse;
import com.desafio.estagio.dto.endereco.EnderecoWithinClienteCreateRequest;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.service.EnderecoService;
import com.desafio.estagio.service.FileService.ImportResult;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class XlsxFileServiceImpl {

    private final ClienteFisicoService clienteFisicoService;
    private final ClienteJuridicoService clienteJuridicoService;
    private final EnderecoService enderecoService;

    // =====================================================================
    // XLSX REPORT
    // =====================================================================

    public byte[] xlsxFisicos() {
        List<ClienteFisicoReportResponse> data = resolveFisicoData(null);
        return toXlsxFisicos(data);
    }

    public byte[] xlsxFisicosPorFiltro(String searchQuery) {
        List<ClienteFisicoReportResponse> data = resolveFisicoData(searchQuery);
        return toXlsxFisicos(data);
    }

    public byte[] xlsxJuridicos() {
        List<ClienteJuridicoReportResponse> data = resolveJuridicoData(null);
        return toXlsxJuridicos(data);
    }

    public byte[] xlsxJuridicosPorFiltro(String searchQuery) {
        List<ClienteJuridicoReportResponse> data = resolveJuridicoData(searchQuery);
        return toXlsxJuridicos(data);
    }

    private byte[] toXlsxFisicos(List<ClienteFisicoReportResponse> data) {
        String[] headers = {"ID", "Nome", "CPF", "RG", "Email", "Data Nasc.", "Ativo", "Criado em"};
        String[] fields = {"id", "nome", "cpf", "rg", "email", "dataNascimento", "estaAtivo", "createdAt"};
        return generateXlsx(data, headers, fields, "Clientes Fisicos");
    }

    private byte[] toXlsxJuridicos(List<ClienteJuridicoReportResponse> data) {
        String[] headers = {"ID", "Razao Social", "CNPJ", "Insc. Estadual", "Email", "Ativo", "Dt. Criacao Emp.", "Criado em"};
        String[] fields = {"id", "razaoSocial", "cnpj", "inscricaoEstadual", "email", "estaAtivo", "dataCriacaoEmpresa", "createdAt"};
        return generateXlsx(data, headers, fields, "Clientes Juridicos");
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

    public byte[] xlsxEnderecos(Long clienteId) {
        List<EnderecoResponse> data = enderecoService.findAllByClienteId(clienteId);
        String[] headers = {"Logradouro", "Numero", "CEP", "Bairro", "Cidade", "UF", "Telefone", "Principal"};
        String[] fields = {"logradouro", "numero", "cep", "bairro", "cidade", "estado", "telefone", "principal"};
        return generateXlsx(data, headers, fields, "Enderecos");
    }

    // =====================================================================
    // IMPORT TEMPLATES (blank XLSX with headers only)
    // =====================================================================

    public byte[] templateFisicosImport() {
        String[] headers = {"CPF", "Nome", "RG", "Email", "Data de Nascimento",
                "Logradouro", "Número", "CEP", "Bairro", "Telefone", "Estado", "Cidade",
                "Principal", "Complemento"};
        return generateTemplate(headers, "Import Clientes Fisicos");
    }

    public byte[] templateJuridicosImport() {
        String[] headers = {"CNPJ", "Razão Social", "Inscrição Estadual", "Email", "Data de Criação",
                "Logradouro", "Número", "CEP", "Bairro", "Telefone", "Estado", "Cidade",
                "Principal", "Complemento"};
        return generateTemplate(headers, "Import Clientes Juridicos");
    }

    public byte[] templateEnderecosImport() {
        String[] headers = {"Logradouro", "Número", "CEP", "Bairro", "Telefone", "Estado", "Cidade",
                "Principal", "Complemento"};
        return generateTemplate(headers, "Import Enderecos");
    }

    // =====================================================================
    // XLSX IMPORT
    // =====================================================================

    public ImportResult importFisicos(InputStream xlsx) {
        List<String> errors = new ArrayList<>();
        int count = 0;

        try (XSSFWorkbook wb = new XSSFWorkbook(xlsx)) {
            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (isRowEmpty(row)) continue;

                int rowNum = row.getRowNum() + 1;

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
                    String msg = translateError(rowNum, e);
                    errors.add(msg);
                    log.warn("Erro ao importar linha {}: {}", rowNum, e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler arquivo XLSX: " + e.getMessage(), e);
        }

        if (!errors.isEmpty()) {
            log.warn("Importação concluída com {} sucessos e {} erros: {}",
                    count, errors.size(), String.join(" | ", errors));
        }
        return new ImportResult(count, errors);
    }

    public ImportResult importJuridicos(InputStream xlsx) {
        List<String> errors = new ArrayList<>();
        int count = 0;

        try (XSSFWorkbook wb = new XSSFWorkbook(xlsx)) {
            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (isRowEmpty(row)) continue;

                int rowNum = row.getRowNum() + 1;

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
                    String msg = translateError(rowNum, e);
                    errors.add(msg);
                    log.warn("Erro ao importar linha {}: {}", rowNum, e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler arquivo XLSX: " + e.getMessage(), e);
        }

        if (!errors.isEmpty()) {
            log.warn("Importação concluída com {} sucessos e {} erros: {}",
                    count, errors.size(), String.join(" | ", errors));
        }
        return new ImportResult(count, errors);
    }

    public ImportResult importEnderecos(Long clienteId, InputStream xlsx) {
        List<String> errors = new ArrayList<>();
        int count = 0;

        try (XSSFWorkbook wb = new XSSFWorkbook(xlsx)) {
            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (isRowEmpty(row)) continue;

                int rowNum = row.getRowNum() + 1;

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
                    String msg = translateError(rowNum, e);
                    errors.add(msg);
                    log.warn("Erro ao importar linha {}: {}", rowNum, e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler arquivo XLSX: " + e.getMessage(), e);
        }

        if (!errors.isEmpty()) {
            log.warn("Importação concluída com {} sucessos e {} erros: {}",
                    count, errors.size(), String.join(" | ", errors));
        }
        return new ImportResult(count, errors);
    }

    // =====================================================================
    // XLSX generators
    // =====================================================================

    private byte[] generateXlsx(List<?> data, String[] headers, String[] fields, String sheetName) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            String safeName = sheetName.length() > 31 ? sheetName.substring(0, 31) : sheetName;
            Sheet sheet = wb.createSheet(safeName);

            CellStyle headerStyle = buildHeaderStyle(wb);
            CellStyle dataStyle = buildDataStyle(wb);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

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

    private byte[] generateTemplate(String[] headers, String sheetName) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            String safeName = sheetName.length() > 31 ? sheetName.substring(0, 31) : sheetName;
            Sheet sheet = wb.createSheet(safeName);

            CellStyle headerStyle = buildHeaderStyle(wb);

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

    private CellStyle buildHeaderStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle buildDataStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
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
                }
            }
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

    // =====================================================================
    // XLSX cell helpers, date parsing, error translation
    // =====================================================================

    private String translateError(int rowNum, Exception e) {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException cve) {
                String constraintName = cve.getConstraintName();
                if (constraintName != null) {
                    return switch (constraintName) {
                        case "che_cep_length" ->
                                "Linha " + rowNum + ": O CEP informado é inválido. Deve conter exatamente 8 dígitos numéricos.";
                        case "che_cep_digits" ->
                                "Linha " + rowNum + ": O CEP informado é inválido. Deve conter apenas números.";
                        case "che_telefone_length" ->
                                "Linha " + rowNum + ": O telefone informado é inválido. Deve ter 10 ou 11 dígitos.";
                        case "che_telefone_digits" ->
                                "Linha " + rowNum + ": O telefone informado é inválido. Deve conter apenas números.";
                        default ->
                                "Linha " + rowNum + ": Erro de integridade dos dados. Verifique os campos obrigatórios.";
                    };
                }
            }
            if (cause instanceof ConstraintViolationException cve) {
                StringBuilder sb = new StringBuilder("Linha ").append(rowNum).append(": ");
                for (var cv : cve.getConstraintViolations()) {
                    String field = cv.getPropertyPath().toString();
                    sb.append(field).append(": ").append(cv.getMessage()).append(". ");
                }
                return sb.toString();
            }
            if (cause instanceof DataIntegrityViolationException) {
                return "Linha " + rowNum + ": Erro de integridade dos dados. Verifique se todos os campos obrigatórios estão preenchidos corretamente.";
            }
            cause = cause.getCause();
        }
        String msg = e.getMessage();
        if (msg == null || msg.isBlank()) {
            return "Linha " + rowNum + ": Erro inesperado. Verifique os dados informados.";
        }
        return "Linha " + rowNum + ": " + msg;
    }

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

        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException ignored) {
        }

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
}
