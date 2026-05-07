package com.desafio.estagio.mvc.model.formatter;

public class CNPJFormatter {

    private static final int CNPJ_RAW_LENGTH = 14;

    /**
     * Private constructor to prevent instantiation (utility class).
     */
    private CNPJFormatter() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Cleans a CNPJ by removing all non-digit characters.
     *
     * @param cnpj The CNPJ to clean (can be formatted or unformatted)
     * @return The cleaned CNPJ containing only digits, or null if input is null
     * @example clean(" 12.345.678 / 0001-90 ") → "12345678000190"
     * clean("12.345.678/0001-90") → "12345678000190"
     */
    public static String unformat(String cnpj) {
        if (cnpj == null) {
            return null;
        }
        return cnpj.replaceAll("\\D", "");
    }

    /**
     * Formats a raw CNPJ into the standard Brazilian format.
     *
     * @param rawCnpj The raw CNPJ (14 digits only)
     * @return The formatted CNPJ (XX.XXX.XXX/XXXX-XX), or the original if invalid
     * @example format(" 12345678000190 ") → "12.345.678/0001-90"
     * format("1234567800019") → "1234567800019" (invalid length)
     */
    public static String format(String rawCnpj) {
        if (rawCnpj == null || rawCnpj.length() != CNPJ_RAW_LENGTH) {
            return rawCnpj;
        }

        // Ensure only digits
        String cleaned = unformat(rawCnpj);
        if (cleaned.length() != CNPJ_RAW_LENGTH) {
            return rawCnpj;
        }

        return String.format("%s.%s.%s/%s-%s",
                cleaned.substring(0, 2),   // First 2 digits (XX)
                cleaned.substring(2, 5),   // Next 3 digits (XXX)
                cleaned.substring(5, 8),   // Next 3 digits (XXX)
                cleaned.substring(8, 12),  // Next 4 digits (XXXX)
                cleaned.substring(12, 14)  // Last 2 digits (XX)
        );
    }

    /**
     * Validates if a CNPJ is valid (11 digits and passes checksum validation).
     *
     * @param cnpj The CNPJ to validate (can be formatted or unformatted)
     * @return true if the CNPJ is valid, false otherwise
     */
    public static boolean isValid(String cnpj) {
        String cleaned = unformat(cnpj);
        if (cleaned == null || cleaned.length() != CNPJ_RAW_LENGTH) {
            return false;
        }

        // Validate check digits
        return validateCheckDigits(cleaned);
    }

    /**
     * Validates the check digits of a CNPJ.
     *
     * @param cnpj The cleaned CNPJ (14 digits)
     * @return true if check digits are valid, false otherwise
     */
    private static boolean validateCheckDigits(String cnpj) {
        // Calculate first check digit
        int sum = 0;
        int[] weights = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        for (int i = 0; i < 12; i++) {
            sum += (cnpj.charAt(i) - '0') * weights[i];
        }

        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) firstDigit = 0;

        // Calculate second check digit
        sum = 0;
        weights = new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        for (int i = 0; i < 13; i++) {
            sum += (cnpj.charAt(i) - '0') * weights[i];
        }

        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) secondDigit = 0;

        // Verify check digits
        return (cnpj.charAt(12) - '0') == firstDigit &&
                (cnpj.charAt(13) - '0') == secondDigit;
    }

    /**
     * Masks a CNPJ showing only first 3 and last 2 digits.
     * Useful for logging or displaying partially hidden CNPJ.
     *
     * @param cnpj The CNPJ to mask
     * @return Masked CNPJ (e.g., "12.345.***/
    /****-90")
     */
    public static String mask(String cnpj) {
        String cleaned = unformat(cnpj);
        if (cleaned == null || cleaned.length() != CNPJ_RAW_LENGTH) {
            return cnpj;
        }

        return String.format("%s.%s.***/****-%s",
                cleaned.substring(0, 2),
                cleaned.substring(2, 5),
                cleaned.substring(12, 14)
        );
    }
}