package com.desafio.estagio.mvc.model.serializer;

import com.desafio.estagio.mvc.model.formatter.CEPFormatter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class CEPFormatDeserializer extends JsonDeserializer<String> {

    private static final Logger log = LoggerFactory.getLogger(CEPFormatDeserializer.class);

    @Override
    public String deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        String cep = parser.getValueAsString();
        log.info("Raw CEP value from request: '{}'", cep);

        if (cep == null || cep.isBlank()) {
            log.warn("CEP is null or blank");
            return null;
        }

        String unformatted = CEPFormatter.unformat(cep);
        log.info("Unformatted CEP: '{}'", unformatted);

        return unformatted;
    }
}