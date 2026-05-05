package com.desafio.estagio.mvc.model.formatter;

public class TelefoneFormatter {

    /**
     * Formats raw telefone to display format
     * Examples:
     * - 11912345678 → (11) 91234-5678
     * - 1134567890  → (11) 3456-7890
     * - 119123456789 → returns as-is (invalid length)
     */
    public static String format(String telefone) {
        if (telefone == null) return null;
        if (telefone.length() == 10) {
            return String.format("(%s) %s-%s",
                    telefone.substring(0, 2),
                    telefone.substring(2, 6),
                    telefone.substring(6, 10));
        } else if (telefone.length() == 11) {
            return String.format("(%s) %s-%s",
                    telefone.substring(0, 2),
                    telefone.substring(2, 7),
                    telefone.substring(7, 11));
        }
        return telefone;
    }

    /**
     * Removes formatting from telefone: (11) 91234-5678 → 11912345678
     */
    public static String unformat(String telefone) {
        return CEPFormatter.unformat(telefone);
    }
}