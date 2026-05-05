package com.desafio.estagio.mvc.model.serializer;

import com.desafio.estagio.mvc.model.formatter.TelefoneFormatter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class TelefoneFormatSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String telefone, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (telefone == null) {
            gen.writeNull();
        } else {
            gen.writeString(TelefoneFormatter.format(telefone));
        }
    }
}