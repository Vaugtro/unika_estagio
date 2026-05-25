package com.desafio.estagio.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Endereco Model Tests")
class EnderecoModelTest {

    private Endereco endereco;
    private TestCliente cliente;

    @BeforeEach
    void setUp() {
        endereco = Endereco.builder()
                .id(1L)
                .logradouro("Rua Principal")
                .numero(123L)
                .cep("12345678")
                .bairro("Centro")
                .telefone("11999999999")
                .cidade("São Paulo")
                .estado("SP")
                .principal(false)
                .build();

        cliente = new TestCliente();
        cliente.setId(1L);
        cliente.setEmail("test@example.com");
    }

    // Concrete test implementation of abstract Cliente
    static class TestCliente extends Cliente {
        @Override
        public void copyFrom(Cliente source) {
            // Simple implementation for testing
        }
    }

    @Nested
    @DisplayName("setCep() Tests")
    class SetCepTests {

        @Test
        @DisplayName("Should set CEP to null when input is null")
        void testSetCepNull() {
            endereco.setCep("12345678");
            endereco.setCep(null);

            assertThat(endereco.getCep()).isNull();
        }

        @Test
        @DisplayName("Should strip non-digits from CEP with hyphen")
        void testStripHyphenFromCep() {
            endereco.setCep("12345-678");

            assertThat(endereco.getCep()).isEqualTo("12345678");
        }

        @Test
        @DisplayName("Should strip non-digits from CEP with dots")
        void testStripDotsFromCep() {
            endereco.setCep("12.345.678");

            assertThat(endereco.getCep()).isEqualTo("12345678");
        }

        @Test
        @DisplayName("Should handle CEP with only digits")
        void testCepWithOnlyDigits() {
            endereco.setCep("12345678");

            assertThat(endereco.getCep()).isEqualTo("12345678");
        }

        @Test
        @DisplayName("Should strip all non-digit characters from CEP")
        void testStripAllNonDigitsFromCep() {
            endereco.setCep("12-345.678");

            assertThat(endereco.getCep()).isEqualTo("12345678");
        }

        @Test
        @DisplayName("Should handle empty string CEP")
        void testEmptyStringCep() {
            endereco.setCep("");

            assertThat(endereco.getCep()).isEmpty();
        }
    }

    @Nested
    @DisplayName("setTelefone() Tests")
    class SetTelefoneTests {

        @Test
        @DisplayName("Should set telefone to null when input is null")
        void testSetTelefoneNull() {
            endereco.setTelefone("11999999999");
            endereco.setTelefone(null);

            assertThat(endereco.getTelefone()).isNull();
        }

        @Test
        @DisplayName("Should strip non-digits from telefone with parentheses and hyphen")
        void testStripFormattedTelefone() {
            endereco.setTelefone("(11) 99999-9999");

            assertThat(endereco.getTelefone()).isEqualTo("11999999999");
        }

        @Test
        @DisplayName("Should strip non-digits from telefone with spaces")
        void testStripSpacesFromTelefone() {
            endereco.setTelefone("11 9 9999 9999");

            assertThat(endereco.getTelefone()).isEqualTo("11999999999");
        }

        @Test
        @DisplayName("Should handle telefone with only digits")
        void testTelefoneWithOnlyDigits() {
            endereco.setTelefone("11999999999");

            assertThat(endereco.getTelefone()).isEqualTo("11999999999");
        }

        @Test
        @DisplayName("Should strip all non-digit characters from telefone")
        void testStripAllNonDigitsFromTelefone() {
            endereco.setTelefone("(11) 9-9999-9999");

            assertThat(endereco.getTelefone()).isEqualTo("11999999999");
        }

        @Test
        @DisplayName("Should handle empty string telefone")
        void testEmptyStringTelefone() {
            endereco.setTelefone("");

            assertThat(endereco.getTelefone()).isEmpty();
        }
    }

    @Nested
    @DisplayName("isPrincipal() Tests")
    class IsPrincipalTests {

        @Test
        @DisplayName("Should return false when principal is false")
        void testIsPrincipalFalse() {
            endereco.setPrincipal(false);

            assertThat(endereco.isPrincipal()).isFalse();
        }

        @Test
        @DisplayName("Should return true when principal is true")
        void testIsPrincipalTrue() {
            endereco.setPrincipal(true);

            assertThat(endereco.isPrincipal()).isTrue();
        }

        @Test
        @DisplayName("Should return default false when not explicitly set")
        void testIsPrincipalDefault() {
            Endereco newEndereco = new Endereco();

            assertThat(newEndereco.isPrincipal()).isFalse();
        }
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should build endereco with all fields")
        void testBuildWithAllFields() {
            Endereco built = Endereco.builder()
                    .id(1L)
                    .logradouro("Rua Teste")
                    .numero(456L)
                    .cep("87654321")
                    .bairro("Vila")
                    .telefone("11888888888")
                    .cidade("Rio de Janeiro")
                    .estado("RJ")
                    .complemento("Apto 101")
                    .principal(true)
                    .build();

            assertThat(built.getId()).isEqualTo(1L);
            assertThat(built.getLogradouro()).isEqualTo("Rua Teste");
            assertThat(built.getNumero()).isEqualTo(456L);
            assertThat(built.getCep()).isEqualTo("87654321");
            assertThat(built.getBairro()).isEqualTo("Vila");
            assertThat(built.getTelefone()).isEqualTo("11888888888");
            assertThat(built.getCidade()).isEqualTo("Rio de Janeiro");
            assertThat(built.getEstado()).isEqualTo("RJ");
            assertThat(built.getComplemento()).isEqualTo("Apto 101");
            assertThat(built.isPrincipal()).isTrue();
        }

        @Test
        @DisplayName("Should build endereco with default principal as false")
        void testBuildDefaultPrincipal() {
            Endereco built = Endereco.builder()
                    .logradouro("Rua Teste")
                    .numero(789L)
                    .cep("11111111")
                    .bairro("Bairro")
                    .telefone("11777777777")
                    .cidade("Belo Horizonte")
                    .estado("MG")
                    .build();

            assertThat(built.isPrincipal()).isFalse();
        }
    }

    @Nested
    @DisplayName("Field Validation Tests")
    class FieldValidationTests {

        @Test
        @DisplayName("Should allow null complemento")
        void testNullComplemento() {
            endereco.setComplemento(null);

            assertThat(endereco.getComplemento()).isNull();
        }

        @Test
        @DisplayName("Should allow setting all required fields")
        void testSetAllRequiredFields() {
            endereco.setLogradouro("Avenida Paulista");
            endereco.setNumero(1000L);
            endereco.setCep("01311100");
            endereco.setBairro("Bela Vista");
            endereco.setTelefone("1133334444");
            endereco.setCidade("São Paulo");
            endereco.setEstado("SP");

            assertThat(endereco.getLogradouro()).isEqualTo("Avenida Paulista");
            assertThat(endereco.getNumero()).isEqualTo(1000L);
            assertThat(endereco.getCep()).isEqualTo("01311100");
            assertThat(endereco.getBairro()).isEqualTo("Bela Vista");
            assertThat(endereco.getTelefone()).isEqualTo("1133334444");
            assertThat(endereco.getCidade()).isEqualTo("São Paulo");
            assertThat(endereco.getEstado()).isEqualTo("SP");
        }
    }

    @Nested
    @DisplayName("Bidirectional Relationship Tests")
    class BidirectionalRelationshipTests {

        @Test
        @DisplayName("Should set cliente reference")
        void testSetClienteReference() {
            endereco.setCliente(cliente);

            assertThat(endereco.getCliente()).isEqualTo(cliente);
        }

        @Test
        @DisplayName("Should clear cliente reference")
        void testClearClienteReference() {
            endereco.setCliente(cliente);
            endereco.setCliente(null);

            assertThat(endereco.getCliente()).isNull();
        }

        @Test
        @DisplayName("Should allow changing cliente reference")
        void testChangeClienteReference() {
            TestCliente cliente1 = new TestCliente();
            cliente1.setId(1L);
            TestCliente cliente2 = new TestCliente();
            cliente2.setId(2L);

            endereco.setCliente(cliente1);
            assertThat(endereco.getCliente()).isEqualTo(cliente1);

            endereco.setCliente(cliente2);
            assertThat(endereco.getCliente()).isEqualTo(cliente2);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle CEP with letters and special characters")
        void testCepWithLettersAndSpecialChars() {
            endereco.setCep("12345-ABC-678");

            assertThat(endereco.getCep()).isEqualTo("12345678");
        }

        @Test
        @DisplayName("Should handle telefone with plus sign and country code")
        void testTelefoneWithCountryCode() {
            endereco.setTelefone("+55 (11) 99999-9999");

            assertThat(endereco.getTelefone()).isEqualTo("5511999999999");
        }

        @Test
        @DisplayName("Should handle very long numero")
        void testVeryLongNumero() {
            endereco.setNumero(999999999L);

            assertThat(endereco.getNumero()).isEqualTo(999999999L);
        }

        @Test
        @DisplayName("Should handle zero numero")
        void testZeroNumero() {
            endereco.setNumero(0L);

            assertThat(endereco.getNumero()).isEqualTo(0L);
        }

        @Test
        @DisplayName("Should handle long logradouro")
        void testLongLogradouro() {
            String longLogradouro = "Rua com nome muito longo que pode ter até 255 caracteres ou mais";
            endereco.setLogradouro(longLogradouro);

            assertThat(endereco.getLogradouro()).isEqualTo(longLogradouro);
        }

        @Test
        @DisplayName("Should handle long complemento")
        void testLongComplemento() {
            String longComplemento = "Complemento com informações adicionais sobre o endereço";
            endereco.setComplemento(longComplemento);

            assertThat(endereco.getComplemento()).isEqualTo(longComplemento);
        }
    }
}
