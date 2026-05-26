package com.desafio.estagio.model.formatter;

public class RGFormatter {

    /**
     * Formats a raw RG to display format.
     * Brazilian RG can have 7 to 9 digits:
     * - 9 digits: XX.XXX.XXX-X
     * - 8 digits: XX.XXX.XX-X
     * - 7 digits:  X.XXX.XX-X
     */
    public static String format(String rg) {
        if (rg == null) return null;
        String cleaned = rg.replaceAll("\\D", "");
        if (cleaned.length() == 9) {
            return String.format("%s.%s.%s-%s",
                    cleaned.substring(0, 2),
                    cleaned.substring(2, 5),
                    cleaned.substring(5, 8),
                    cleaned.substring(8, 9));
        } else if (cleaned.length() == 8) {
            return String.format("%s.%s.%s-%s",
                    cleaned.substring(0, 2),
                    cleaned.substring(2, 5),
                    cleaned.substring(5, 7),
                    cleaned.substring(7, 8));
        } else if (cleaned.length() == 7) {
            return String.format("%s.%s-%s",
                    cleaned.substring(0, 1),
                    cleaned.substring(1, 4),
                    cleaned.substring(4, 7));
        }
        return rg;
    }

    /**
     * Removes formatting from RG: XX.XXX.XXX-X → XXXXXXXX
     */
    public static String unformat(String rg) {
        if (rg == null) return null;
        return rg.replaceAll("\\D", "");
    }
}
