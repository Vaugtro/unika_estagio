package com.desafio.estagio.mvc.model.serializer;

import com.desafio.estagio.mvc.model.formatter.CNPJFormatter;
import com.desafio.estagio.mvc.model.formatter.CPFFormatter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class CNPJFormatSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        if (value == null || value.length() != 14) {
            gen.writeString(value);
        } else {
            gen.writeString(CNPJFormatter.format(value));
        }
    }
}