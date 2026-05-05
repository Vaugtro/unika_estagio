package com.desafio.estagio.mvc.model.serializer;

import com.desafio.estagio.mvc.model.formatter.CPFFormatter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class CPFFormatDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        String cpf = parser.getValueAsString();
        if (cpf == null || cpf.isBlank()) {
            return null;
        }
        // Store raw numbers in database: 123.456.789-01 → 12345678901
        return CPFFormatter.unformat(cpf);
    }
}