package com.desafio.estagio.mvc.controller;

import com.desafio.estagio.mvc.model.dto.TipoCliente;
import com.desafio.estagio.mvc.service.JasperReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Endpoints para exportação de relatórios")
public class JasperReportController {

    private final JasperReportService reportService;

    @GetMapping("/clientes/fisicos/pdf")
    @Operation(summary = "Exportar lista de clientes físicos para PDF")
    public ResponseEntity<byte[]> exportClientesFisicosToPdf() {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportTitle", "Relatório de Clientes Físicos");
        parameters.put("generatedDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        byte[] pdfReport = reportService.generateForCliente("ClientesFisicosReport.jrxml", parameters, TipoCliente.FISICA);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ClientesFisicosReport.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfReport);
    }

    @GetMapping("/clientes/juridicos/pdf")
    @Operation(summary = "Exportar lista de clientes jurídicos para PDF")
    public ResponseEntity<byte[]> exportClientesJuridicosToPdf() {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportTitle", "Relatório de Clientes Jurídicos");
        parameters.put("generatedDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        byte[] pdfReport = reportService.generateForCliente("ClientesJuridicosReport", parameters, TipoCliente.JURIDICA);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ClientesJuridicosReport.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfReport);
    }
}