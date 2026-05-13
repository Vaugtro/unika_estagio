package com.desafio.estagio.dto.endereco.sanitizer;

public final class TelefoneSanitizer {

    private TelefoneSanitizer() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String sanitize(String cep) {
        return cep != null ? cep.replaceAll("\\D", "") : null;
    }
}