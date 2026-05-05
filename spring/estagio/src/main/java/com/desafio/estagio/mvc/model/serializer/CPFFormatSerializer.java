package com.desafio.estagio.mvc.model.serializer;

import com.desafio.estagio.mvc.model.formatter.CPFFormatter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class CPFFormatSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String cpf, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (cpf == null) {
            gen.writeNull();
        } else {
            // Format: 12345678901 → 123.456.789-01
            gen.writeString(CPFFormatter.format(cpf));
        }
    }
}