package com.desafio.estagio.controller;

import com.desafio.estagio.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/export")
@RequiredArgsConstructor
@Tag(name = "Exportação", description = "Endpoints para exportação de relatórios (PDF, XLSX)")
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/clientes/fisicos/pdf")
    @Operation(summary = "Exportar lista de clientes físicos para PDF")
    public ResponseEntity<byte[]> exportClientesFisicosToPdf() {
        byte[] pdfReport = exportService.pdfFisicos();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ClientesFisicosReport.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfReport);
    }

    @GetMapping("/clientes/juridicos/pdf")
    @Operation(summary = "Exportar lista de clientes jurídicos para PDF")
    public ResponseEntity<byte[]> exportClientesJuridicosToPdf() {
        byte[] pdfReport = exportService.pdfJuridicos();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ClientesJuridicosReport.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfReport);
    }
}
