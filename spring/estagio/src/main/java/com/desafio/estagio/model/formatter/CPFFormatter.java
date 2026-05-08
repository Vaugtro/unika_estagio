package com.desafio.estagio.model.formatter;

public class CPFFormatter {

    /**
     * Formats a raw CPF to display format: 12345678901 → 123.456.789-01
     */
    public static String format(String cpf) {
        if (cpf == null || cpf.isBlank()) return null;

        // Remove all non-digits
        String numbers = cpf.replaceAll("\\D", "");

        if (numbers.length() != 11) return cpf; // Return original if invalid

        return String.format("%s.%s.%s-%s",
                numbers.substring(0, 3),
                numbers.substring(3, 6),
                numbers.substring(6, 9),
                numbers.substring(9, 11));
    }

    /**
     * Removes formatting from CPF: 123.456.789-01 → 12345678901
     */
    public static String unformat(String cpf) {
        return CEPFormatter.unformat(cpf);
    }
}