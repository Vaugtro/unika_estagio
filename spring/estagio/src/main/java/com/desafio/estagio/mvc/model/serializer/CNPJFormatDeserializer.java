package com.desafio.estagio.mvc.model.serializer;

import com.desafio.estagio.mvc.model.formatter.CEPFormatter;
import com.desafio.estagio.mvc.model.formatter.CNPJFormatter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class CNPJFormatDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        return CNPJFormatter.unformat(value);  // "12.345.678/0001-90" → "12345678000190"
    }
}