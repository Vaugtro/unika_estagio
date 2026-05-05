package com.desafio.estagio.mvc.model.formatter;

public class CEPFormatter {

    /**
     * Formats raw CEP to display format: 01234567 → 01234-567
     */
    public static String format(String cep) {
        if (cep == null || cep.length() != 8) return cep;
        return cep.substring(0, 5) + "-" + cep.substring(5);
    }


    /**
     * Removes formatting from CEP: 01234-567 → 01234567
     */
    public static String unformat(String cep) {
        if (cep == null) return null;
        return cep.replaceAll("\\D", "");
    }
}