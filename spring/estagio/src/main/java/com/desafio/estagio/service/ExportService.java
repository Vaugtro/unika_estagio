package com.desafio.estagio.service;

/**
 * Wicket-facing export façade. Generates PDF (via JasperReports) and XLSX (via Apache POI)
 * for the three exportable entities.
 */
public interface ExportService {

    /**
     * PDF of all ClienteFisico records.
     */
    byte[] pdfFisicos();

    /**
     * PDF of all ClienteJuridico records.
     */
    byte[] pdfJuridicos();

    /**
     * PDF of all Enderecos belonging to the given cliente.
     */
    byte[] pdfEnderecos(Long clienteId);

    /**
     * XLSX of all ClienteFisico records.
     */
    byte[] xlsxFisicos();

    /**
     * XLSX of all ClienteJuridico records.
     */
    byte[] xlsxJuridicos();

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
     * @return number of successfully imported rows
     */
    int importFisicos(java.io.InputStream xlsx);

    /**
     * Imports ClienteJuridico records from an XLSX file.
     * Each row contains ClienteJuridico fields + Endereco fields.
     *
     * @return number of successfully imported rows
     */
    int importJuridicos(java.io.InputStream xlsx);

    /**
     * Imports Endereco records for the given cliente from an XLSX file.
     *
     * @return number of successfully imported rows
     */
    int importEnderecos(Long clienteId, java.io.InputStream xlsx);
}
