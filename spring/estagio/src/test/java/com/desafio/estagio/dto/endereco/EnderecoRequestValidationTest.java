package com.desafio.estagio.dto.endereco;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EnderecoRequest Validation Tests")
class EnderecoRequestValidationTest {

    private static Validator validator;
    private static ValidatorFactory factory;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        if (factory != null) {
            factory.close();
        }
    }

    // =====================================================
    // EnderecoCreateRequest — telefone optional
    // =====================================================

    @Test
    @DisplayName("EnderecoCreateRequest: null telefone should pass validation")
    void testCreateRequestNullTelefone() {
        EnderecoCreateRequest request = EnderecoCreateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("01001-000")
                .bairro("Centro")
                .telefone(null)
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .clienteId(1L)
                .build();

        Set<ConstraintViolation<EnderecoCreateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("EnderecoCreateRequest: blank telefone should pass validation")
    void testCreateRequestBlankTelefone() {
        EnderecoCreateRequest request = EnderecoCreateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("01001-000")
                .bairro("Centro")
                .telefone("")
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .clienteId(1L)
                .build();

        Set<ConstraintViolation<EnderecoCreateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("EnderecoCreateRequest: valid telefone should pass validation")
    void testCreateRequestValidTelefone() {
        EnderecoCreateRequest request = EnderecoCreateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("01001-000")
                .bairro("Centro")
                .telefone("(11) 91234-5678")
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .clienteId(1L)
                .build();

        Set<ConstraintViolation<EnderecoCreateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("EnderecoCreateRequest: missing required fields should fail validation")
    void testCreateRequestMissingRequiredFields() {
        EnderecoCreateRequest request = EnderecoCreateRequest.builder()
                .logradouro(null)
                .numero(null)
                .cep(null)
                .bairro(null)
                .telefone(null)
                .estado(null)
                .cidade(null)
                .clienteId(null)
                .build();

        Set<ConstraintViolation<EnderecoCreateRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertThat(messages).anyMatch(m -> m.contains("Logradouro"));
        assertThat(messages).anyMatch(m -> m.contains("Número"));
        assertThat(messages).anyMatch(m -> m.contains("CEP"));
        assertThat(messages).anyMatch(m -> m.contains("Bairro"));
        assertThat(messages).anyMatch(m -> m.contains("Estado"));
        assertThat(messages).anyMatch(m -> m.contains("Cidade"));
        assertThat(messages).anyMatch(m -> m.contains("cliente"));
    }

    // =====================================================
    // EnderecoWithinClienteCreateRequest — telefone optional
    // =====================================================

    @Test
    @DisplayName("EnderecoWithinClienteCreateRequest: null telefone should pass validation")
    void testWithinClienteCreateRequestNullTelefone() {
        EnderecoWithinClienteCreateRequest request = EnderecoWithinClienteCreateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("01001-000")
                .bairro("Centro")
                .telefone(null)
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .build();

        Set<ConstraintViolation<EnderecoWithinClienteCreateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("EnderecoWithinClienteCreateRequest: blank telefone should pass validation")
    void testWithinClienteCreateRequestBlankTelefone() {
        EnderecoWithinClienteCreateRequest request = EnderecoWithinClienteCreateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("01001-000")
                .bairro("Centro")
                .telefone("")
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .build();

        Set<ConstraintViolation<EnderecoWithinClienteCreateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    // =====================================================
    // EnderecoUpdateRequest — telefone optional
    // =====================================================

    @Test
    @DisplayName("EnderecoUpdateRequest: null telefone should pass validation")
    void testUpdateRequestNullTelefone() {
        EnderecoUpdateRequest request = EnderecoUpdateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("01001-000")
                .bairro("Centro")
                .telefone(null)
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .build();

        Set<ConstraintViolation<EnderecoUpdateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("EnderecoUpdateRequest: blank telefone should pass validation")
    void testUpdateRequestBlankTelefone() {
        EnderecoUpdateRequest request = EnderecoUpdateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("01001-000")
                .bairro("Centro")
                .telefone("")
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .build();

        Set<ConstraintViolation<EnderecoUpdateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("EnderecoUpdateRequest: valid telefone should pass validation")
    void testUpdateRequestValidTelefone() {
        EnderecoUpdateRequest request = EnderecoUpdateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("01001-000")
                .bairro("Centro")
                .telefone("(11) 91234-5678")
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .build();

        Set<ConstraintViolation<EnderecoUpdateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }
}
