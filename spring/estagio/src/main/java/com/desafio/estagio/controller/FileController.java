package com.desafio.estagio.controller;

import com.desafio.estagio.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "Exportacao", description = "Endpoints para exportação de relatórios (PDF, XLSX) e templates de importação")
public class FileController {

    private final FileService fileService;

    // =====================================================================
    // PDF
    // =====================================================================

    @GetMapping(value = "/clientes/fisicos/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Exportar lista de clientes físicos para PDF")
    @ApiResponse(responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PDF_VALUE,
                    schema = @Schema(type = "string", format = "byte")
            ))
    public ResponseEntity<byte[]> exportClientesFisicosToPdf() {
        byte[] pdfReport = fileService.pdfFisicos();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ClientesFisicosReport.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfReport);
    }

    @GetMapping(value = "/clientes/juridicos/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Exportar lista de clientes jurídicos para PDF")
    @ApiResponse(responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PDF_VALUE,
                    schema = @Schema(type = "string", format = "byte")
            ))
    public ResponseEntity<byte[]> exportClientesJuridicosToPdf() {
        byte[] pdfReport = fileService.pdfJuridicos();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ClientesJuridicosReport.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfReport);
    }

    // =====================================================================
    // XLSX
    // =====================================================================

    @GetMapping(value = "/clientes/fisicos/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(summary = "Exportar lista de clientes físicos para XLSX")
    @ApiResponse(responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    schema = @Schema(type = "string", format = "byte")
            ))
    public ResponseEntity<byte[]> exportClientesFisicosToXlsx() {
        byte[] xlsx = fileService.xlsxFisicos();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ClientesFisicos.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(xlsx);
    }

    @GetMapping(value = "/clientes/juridicos/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(summary = "Exportar lista de clientes jurídicos para XLSX")
    @ApiResponse(responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    schema = @Schema(type = "string", format = "byte")
            ))
    public ResponseEntity<byte[]> exportClientesJuridicosToXlsx() {
        byte[] xlsx = fileService.xlsxJuridicos();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ClientesJuridicos.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(xlsx);
    }

    // =====================================================================
    // TEMPLATES
    // =====================================================================

    @GetMapping(value = "/clientes/fisicos/template", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(summary = "Baixar template XLSX para importação de clientes físicos")
    @ApiResponse(responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    schema = @Schema(type = "string", format = "byte")
            ))
    public ResponseEntity<byte[]> templateClientesFisicos() {
        byte[] template = fileService.templateFisicosImport();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template-clientes-fisicos.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(template);
    }

    @GetMapping(value = "/clientes/juridicos/template", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(summary = "Baixar template XLSX para importação de clientes jurídicos")
    @ApiResponse(responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    schema = @Schema(type = "string", format = "byte")
            ))
    public ResponseEntity<byte[]> templateClientesJuridicos() {
        byte[] template = fileService.templateJuridicosImport();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template-clientes-juridicos.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(template);
    }
}
