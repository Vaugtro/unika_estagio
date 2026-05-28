package com.desafio.estagio.controller;

import com.desafio.estagio.service.FileService;
import com.desafio.estagio.service.FileService.ImportResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/v1/export")
@RequiredArgsConstructor
@Tag(name = "Arquivo", description = "Endpoints para exportação de relatórios (PDF, XLSX), templates e importação")
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
    public ResponseEntity<byte[]> exportClientesFisicosToPdf(@RequestParam(name = "q", required = false) String query) {
        byte[] pdfReport = fileService.pdfFisicosPorFiltro(query);

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
    public ResponseEntity<byte[]> exportClientesJuridicosToPdf(@RequestParam(name = "q", required = false) String query) {
        byte[] pdfReport = fileService.pdfJuridicosPorFiltro(query);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ClientesJuridicosReport.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfReport);
    }

    @GetMapping(value = "/clientes/{clienteId}/enderecos/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Exportar endereços de um cliente para PDF")
    @ApiResponse(responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PDF_VALUE,
                    schema = @Schema(type = "string", format = "byte")
            ))
    public ResponseEntity<byte[]> exportEnderecosToPdf(@PathVariable Long clienteId) {
        byte[] pdfReport = fileService.pdfEnderecos(clienteId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=EnderecosReport.pdf")
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
    public ResponseEntity<byte[]> exportClientesFisicosToXlsx(@RequestParam(name = "q", required = false) String query) {
        byte[] xlsx = fileService.xlsxFisicosPorFiltro(query);

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
    public ResponseEntity<byte[]> exportClientesJuridicosToXlsx(@RequestParam(name = "q", required = false) String query) {
        byte[] xlsx = fileService.xlsxJuridicosPorFiltro(query);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ClientesJuridicos.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(xlsx);
    }

    @GetMapping(value = "/clientes/{clienteId}/enderecos/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(summary = "Exportar endereços de um cliente para XLSX")
    @ApiResponse(responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    schema = @Schema(type = "string", format = "byte")
            ))
    public ResponseEntity<byte[]> exportEnderecosToXlsx(@PathVariable Long clienteId) {
        byte[] xlsx = fileService.xlsxEnderecos(clienteId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Enderecos.xlsx")
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

    @GetMapping(value = "/enderecos/template", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(summary = "Baixar template XLSX para importação de endereços")
    @ApiResponse(responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    schema = @Schema(type = "string", format = "byte")
            ))
    public ResponseEntity<byte[]> templateEnderecos() {
        byte[] template = fileService.templateEnderecosImport();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template-enderecos.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(template);
    }

    // =====================================================================
    // IMPORT
    // =====================================================================

    @PostMapping(value = "/clientes/fisicos/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importar clientes físicos via XLSX")
    @ApiResponse(responseCode = "200",
            description = "Resultado da importação",
            content = @Content(schema = @Schema(implementation = ImportResult.class)))
    public ResponseEntity<ImportResult> importClientesFisicos(@RequestParam("file") MultipartFile file) throws IOException {
        ImportResult result = fileService.importFisicos(file.getInputStream());
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/clientes/juridicos/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importar clientes jurídicos via XLSX")
    @ApiResponse(responseCode = "200",
            description = "Resultado da importação",
            content = @Content(schema = @Schema(implementation = ImportResult.class)))
    public ResponseEntity<ImportResult> importClientesJuridicos(@RequestParam("file") MultipartFile file) throws IOException {
        ImportResult result = fileService.importJuridicos(file.getInputStream());
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/enderecos/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importar endereços de um cliente via XLSX")
    @ApiResponse(responseCode = "200",
            description = "Resultado da importação",
            content = @Content(schema = @Schema(implementation = ImportResult.class)))
    public ResponseEntity<ImportResult> importEnderecos(
            @RequestParam("clienteId") Long clienteId,
            @RequestParam("file") MultipartFile file) throws IOException {
        ImportResult result = fileService.importEnderecos(clienteId, file.getInputStream());
        return ResponseEntity.ok(result);
    }
}
