package com.desafio.estagio.mvc.model.serializer;

import com.desafio.estagio.mvc.model.formatter.TelefoneFormatter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class TelefoneFormatDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        String telefone = parser.getValueAsString();
        if (telefone == null || telefone.isBlank()) {
            return null;
        }
        return TelefoneFormatter.unformat(telefone);
    }
}