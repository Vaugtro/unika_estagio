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
}
