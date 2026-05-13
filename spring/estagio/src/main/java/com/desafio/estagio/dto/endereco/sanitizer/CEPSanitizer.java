package com.desafio.estagio.dto.endereco.sanitizer;

public final class CEPSanitizer {

    private CEPSanitizer() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String sanitize(String cep) {
        return cep != null ? cep.replaceAll("\\D", "") : null;
    }
}