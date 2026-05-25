package com.desafio.estagio.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class OpenApiCustomizerConfig {

    @Bean
    public OpenApiCustomizer operationIdCustomizer() {
        return openApi -> {
            // Build a map of tag name -> sanitized prefix (e.g., "Clientes Físicos" -> "ClientesFisicos")
            Map<String, String> tagPrefixes = openApi.getTags().stream()
                    .collect(Collectors.toMap(
                            Tag::getName,
                            tag -> sanitize(tag.getName())
                    ));

            openApi.getPaths().forEach((path, pathItem) -> {
                pathItem.readOperations().forEach(operation -> {
                    String originalId = operation.getOperationId();
                    if (originalId == null || originalId.isBlank()) {
                        // Fallback: generate from HTTP method + path if no operationId exists
                        originalId = generateFallbackId(pathItem, operation);
                    }

                    // Get the first tag (primary tag) or fallback to "Default"
                    List<String> tags = operation.getTags();
                    String prefix = (tags != null && !tags.isEmpty())
                            ? tagPrefixes.getOrDefault(tags.get(0), sanitize(tags.get(0)))
                            : "Default";

                    // Remove existing numeric suffixes like _1, _2 that SpringDoc added
                    String cleanId = originalId.replaceAll("_\\d+$", "");

                    // Set new operationId: TagName_methodName
                    operation.setOperationId(prefix + "_" + cleanId);
                });
            });
        };
    }

    private String sanitize(String input) {
        // Decompose accents: "Endereços" -> "Enderecos", "Físicos" -> "Fisicos"
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return normalized
                .replaceAll("[^a-zA-Z0-9\\s]", "")  // Now safe for ASCII
                .replaceAll("\\s+", "")             // Remove spaces
                .trim();
    }

    private String generateFallbackId(io.swagger.v3.oas.models.PathItem pathItem, Operation operation) {
        // Determine HTTP method
        String method = "request";
        if (pathItem.getGet() == operation) method = "get";
        else if (pathItem.getPost() == operation) method = "create";
        else if (pathItem.getPut() == operation) method = "update";
        else if (pathItem.getDelete() == operation) method = "delete";
        else if (pathItem.getPatch() == operation) method = "patch";

        return method;
    }
}
