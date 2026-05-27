package com.desafio.estagio.service;

import java.io.InputStream;
import java.util.List;

/**
 * Wicket-facing file façade. Generates PDF (via JasperReports) and XLSX (via Apache POI)
 * for the three exportable entities.
 */
public interface FileService {

    record ImportResult(int successCount, List<String> errors) {}

    // =====================================================================
    // PDF
    // =====================================================================

    /**
     * PDF of all ClienteFisico records.
     */
    byte[] pdfFisicos();

    /**
     * PDF of ClienteFisico records filtered by search query.
     */
    byte[] pdfFisicosPorFiltro(String searchQuery);

    /**
     * PDF of all ClienteJuridico records.
     */
    byte[] pdfJuridicos();

    /**
     * PDF of ClienteJuridico records filtered by search query.
     */
    byte[] pdfJuridicosPorFiltro(String searchQuery);

    /**
     * PDF of all Enderecos belonging to the given cliente.
     */
    byte[] pdfEnderecos(Long clienteId);

    // =====================================================================
    // XLSX REPORT
    // =====================================================================

    /**
     * XLSX of all ClienteFisico records.
     */
    byte[] xlsxFisicos();

    /**
     * XLSX of ClienteFisico records filtered by search query.
     */
    byte[] xlsxFisicosPorFiltro(String searchQuery);

    /**
     * XLSX of all ClienteJuridico records.
     */
    byte[] xlsxJuridicos();

    /**
     * XLSX of ClienteJuridico records filtered by search query.
     */
    byte[] xlsxJuridicosPorFiltro(String searchQuery);

    /**
     * XLSX of all Enderecos belonging to the given cliente.
     */
    byte[] xlsxEnderecos(Long clienteId);

    // =====================================================================
    // IMPORT TEMPLATES (blank XLSX with headers only)
    // =====================================================================

    /**
     * Blank XLSX template for importing ClienteFisico + Endereco (same row).
     */
    byte[] templateFisicosImport();

    /**
     * Blank XLSX template for importing ClienteJuridico + Endereco (same row).
     */
    byte[] templateJuridicosImport();

    /**
     * Blank XLSX template for importing Enderecos only (for cliente/detalhe screen).
     */
    byte[] templateEnderecosImport();

    // =====================================================================
    // XLSX IMPORT
    // =====================================================================

    /**
     * Imports ClienteFisico records from an XLSX file.
     * Each row contains ClienteFisico fields + Endereco fields.
     *
     * @return ImportResult with success count and list of error messages (with row number and field info)
     */
    ImportResult importFisicos(java.io.InputStream xlsx);

    /**
     * Imports ClienteJuridico records from an XLSX file.
     * Each row contains ClienteJuridico fields + Endereco fields.
     *
     * @return ImportResult with success count and list of error messages (with row number and field info)
     */
    ImportResult importJuridicos(java.io.InputStream xlsx);

    /**
     * Imports Endereco records for the given cliente from an XLSX file.
     *
     * @return ImportResult with success count and list of error messages (with row number and field info)
     */
    ImportResult importEnderecos(Long clienteId, java.io.InputStream xlsx);
}
