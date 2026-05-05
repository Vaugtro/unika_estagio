package com.desafio.estagio.mvc.model.serializer;

import com.desafio.estagio.mvc.model.formatter.CEPFormatter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class CEPFormatSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String cep, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (cep == null) {
            gen.writeNull();
        } else {
            gen.writeString(CEPFormatter.format(cep));
        }
    }
}