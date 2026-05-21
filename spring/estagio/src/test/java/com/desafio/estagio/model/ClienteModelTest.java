package com.desafio.estagio.model;

import com.desafio.estagio.model.enums.TipoCliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Cliente Model Tests")
class ClienteModelTest {

    // Concrete test implementation of abstract Cliente
    static class TestCliente extends Cliente {
        @Override
        public void copyFrom(Cliente source) {
            if (!(source instanceof TestCliente s)) {
                throw new IllegalArgumentException("Source must be an instance of TestCliente");
            }
            // Simple implementation for testing
        }
    }

    private TestCliente cliente;
    private Endereco endereco1;
    private Endereco endereco2;
    private Endereco endereco3;

    @BeforeEach
    void setUp() {
        cliente = new TestCliente();
        cliente.setId(1L);
        cliente.setTipo(TipoCliente.FISICA);
        cliente.setEmail("test@example.com");
        cliente.setEstaAtivo(true);

        endereco1 = Endereco.builder()
                .id(1L)
                .logradouro("Rua A")
                .numero(100L)
                .cep("12345678")
                .bairro("Centro")
                .telefone("11999999999")
                .cidade("São Paulo")
                .estado("SP")
                .principal(false)
                .build();

        endereco2 = Endereco.builder()
                .id(2L)
                .logradouro("Rua B")
                .numero(200L)
                .cep("87654321")
                .bairro("Vila")
                .telefone("11888888888")
                .cidade("Rio de Janeiro")
                .estado("RJ")
                .principal(false)
                .build();

        endereco3 = Endereco.builder()
                .id(3L)
                .logradouro("Rua C")
                .numero(300L)
                .cep("11111111")
                .bairro("Bairro")
                .telefone("11777777777")
                .cidade("Belo Horizonte")
                .estado("MG")
                .principal(false)
                .build();
    }

    @Nested
    @DisplayName("addEndereco() Tests")
    class AddEnderecoTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when endereco is null")
        void testAddNullEndereco() {
            assertThatThrownBy(() -> cliente.addEndereco(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Endereço não pode ser nulo");
        }

        @Test
        @DisplayName("Should set first endereco as principal automatically")
        void testFirstEnderecoBecomePrincipal() {
            cliente.addEndereco(endereco1);

            assertThat(cliente.getEnderecos()).hasSize(1);
            assertThat(endereco1.isPrincipal()).isTrue();
            assertThat(endereco1.getCliente()).isEqualTo(cliente);
        }

        @Test
        @DisplayName("Should demote all others when adding new principal")
        void testNewPrincipalDemotesOthers() {
            cliente.addEndereco(endereco1);
            endereco2.setPrincipal(true);

            cliente.addEndereco(endereco2);

            assertThat(endereco1.isPrincipal()).isFalse();
            assertThat(endereco2.isPrincipal()).isTrue();
            assertThat(cliente.getEnderecos()).hasSize(2);
        }

        @Test
        @DisplayName("Should add non-principal endereco without affecting existing principal")
        void testAddNonPrincipalEndereco() {
            cliente.addEndereco(endereco1);
            endereco2.setPrincipal(false);

            cliente.addEndereco(endereco2);

            assertThat(endereco1.isPrincipal()).isTrue();
            assertThat(endereco2.isPrincipal()).isFalse();
            assertThat(cliente.getEnderecos()).hasSize(2);
        }

        @Test
        @DisplayName("Should establish bidirectional relationship")
        void testBidirectionalRelationship() {
            cliente.addEndereco(endereco1);

            assertThat(endereco1.getCliente()).isEqualTo(cliente);
            assertThat(cliente.getEnderecos()).contains(endereco1);
        }
    }

    @Nested
    @DisplayName("removeEndereco() Tests")
    class RemoveEnderecoTests {

        @Test
        @DisplayName("Should throw IllegalStateException when enderecos list is null or empty")
        void testRemoveFromEmptyList() {
            // cliente already has empty enderecos list from setUp

            assertThatThrownBy(() -> cliente.removeEndereco(endereco1))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cliente não possui endereços para remover");
        }

        @Test
        @DisplayName("Should throw IllegalStateException when only one endereco remains")
        void testRemoveOnlyEndereco() {
            cliente.addEndereco(endereco1);

            assertThatThrownBy(() -> cliente.removeEndereco(endereco1))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cliente deve ter pelo menos um endereço. Não é possível remover o único endereço.");
        }

        @Test
        @DisplayName("Should promote first remaining when removing principal")
        void testRemovePrincipalPromotesFirst() {
            cliente.addEndereco(endereco1);
            cliente.addEndereco(endereco2);
            cliente.addEndereco(endereco3);

            assertThat(endereco1.isPrincipal()).isTrue();

            cliente.removeEndereco(endereco1);

            assertThat(cliente.getEnderecos()).hasSize(2);
            assertThat(endereco2.isPrincipal()).isTrue();
            assertThat(endereco1.getCliente()).isNull();
        }

        @Test
        @DisplayName("Should remove non-principal endereco without affecting principal")
        void testRemoveNonPrincipalEndereco() {
            cliente.addEndereco(endereco1);
            cliente.addEndereco(endereco2);

            cliente.removeEndereco(endereco2);

            assertThat(cliente.getEnderecos()).hasSize(1);
            assertThat(endereco1.isPrincipal()).isTrue();
            assertThat(endereco2.getCliente()).isNull();
        }

        @Test
        @DisplayName("Should clear cliente reference when removing")
        void testClearClienteReference() {
            cliente.addEndereco(endereco1);
            cliente.addEndereco(endereco2);

            cliente.removeEndereco(endereco2);

            assertThat(endereco2.getCliente()).isNull();
        }
    }

    @Nested
    @DisplayName("getEnderecoPrincipal() Tests")
    class GetEnderecoPrincipalTests {

        @Test
        @DisplayName("Should return null when enderecos list is empty")
        void testGetPrincipalFromEmpty() {
            assertThat(cliente.getEnderecoPrincipal()).isNull();
        }

        @Test
        @DisplayName("Should return principal endereco when marked")
        void testGetMarkedPrincipal() {
            cliente.addEndereco(endereco1);
            cliente.addEndereco(endereco2);
            cliente.setEnderecoPrincipal(endereco2);

            assertThat(cliente.getEnderecoPrincipal()).isEqualTo(endereco2);
        }

        @Test
        @DisplayName("Should return first endereco when none marked as principal")
        void testGetFirstWhenNonePrincipal() {
            endereco1.setPrincipal(false);
            endereco2.setPrincipal(false);
            cliente.addEndereco(endereco1);
            cliente.addEndereco(endereco2);

            // Reset principal flags (addEndereco sets first as principal)
            endereco1.setPrincipal(false);
            endereco2.setPrincipal(false);

            assertThat(cliente.getEnderecoPrincipal()).isEqualTo(endereco1);
        }
    }

    @Nested
    @DisplayName("setEnderecoPrincipal() Tests")
    class SetEnderecoPrincipalTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when endereco is null")
        void testSetNullAsPrincipal() {
            cliente.addEndereco(endereco1);

            assertThatThrownBy(() -> cliente.setEnderecoPrincipal(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Endereço não pode ser nulo");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when endereco not in list")
        void testSetPrincipalNotInList() {
            cliente.addEndereco(endereco1);

            assertThatThrownBy(() -> cliente.setEnderecoPrincipal(endereco2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Endereço não pertence a este cliente");
        }

        @Test
        @DisplayName("Should demote all others when setting new principal")
        void testSetPrincipalDemotesOthers() {
            cliente.addEndereco(endereco1);
            cliente.addEndereco(endereco2);
            cliente.addEndereco(endereco3);

            assertThat(endereco1.isPrincipal()).isTrue();

            cliente.setEnderecoPrincipal(endereco3);

            assertThat(endereco1.isPrincipal()).isFalse();
            assertThat(endereco2.isPrincipal()).isFalse();
            assertThat(endereco3.isPrincipal()).isTrue();
        }
    }

    @Nested
    @DisplayName("setEnderecos() Tests")
    class SetEnderecosTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when list is null")
        void testSetNullEnderecos() {
            assertThatThrownBy(() -> cliente.setEnderecos(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cliente deve ter pelo menos um endereço");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when list is empty")
        void testSetEmptyEnderecos() {
            assertThatThrownBy(() -> cliente.setEnderecos(new ArrayList<>()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cliente deve ter pelo menos um endereço");
        }

        @Test
        @DisplayName("Should auto-promote first when no principal exists")
        void testAutoPromoteFirstWhenNoPrincipal() {
            endereco1.setPrincipal(false);
            endereco2.setPrincipal(false);
            List<Endereco> enderecos = List.of(endereco1, endereco2);

            cliente.setEnderecos(enderecos);

            assertThat(endereco1.isPrincipal()).isTrue();
            assertThat(endereco2.isPrincipal()).isFalse();
        }

        @Test
        @DisplayName("Should preserve existing principal")
        void testPreserveExistingPrincipal() {
            endereco1.setPrincipal(false);
            endereco2.setPrincipal(true);
            List<Endereco> enderecos = List.of(endereco1, endereco2);

            cliente.setEnderecos(enderecos);

            assertThat(endereco1.isPrincipal()).isFalse();
            assertThat(endereco2.isPrincipal()).isTrue();
        }

        @Test
        @DisplayName("Should establish bidirectional relationships")
        void testBidirectionalRelationships() {
            List<Endereco> enderecos = List.of(endereco1, endereco2);

            cliente.setEnderecos(enderecos);

            assertThat(endereco1.getCliente()).isEqualTo(cliente);
            assertThat(endereco2.getCliente()).isEqualTo(cliente);
            assertThat(cliente.getEnderecos()).containsExactly(endereco1, endereco2);
        }

        @Test
        @DisplayName("Should clear old relationships when replacing")
        void testClearOldRelationships() {
            cliente.addEndereco(endereco1);
            assertThat(endereco1.getCliente()).isEqualTo(cliente);

            List<Endereco> newEnderecos = List.of(endereco2, endereco3);
            cliente.setEnderecos(newEnderecos);

            assertThat(endereco1.getCliente()).isNull();
            assertThat(endereco2.getCliente()).isEqualTo(cliente);
            assertThat(endereco3.getCliente()).isEqualTo(cliente);
        }
    }

    @Nested
    @DisplayName("hasEnderecoPrincipal() Tests")
    class HasEnderecoPrincipalTests {

        @Test
        @DisplayName("Should return false when no enderecos")
        void testHasNoPrincipal() {
            assertThat(cliente.hasEnderecoPrincipal()).isFalse();
        }

        @Test
        @DisplayName("Should return true when principal exists")
        void testHasPrincipal() {
            cliente.addEndereco(endereco1);

            assertThat(cliente.hasEnderecoPrincipal()).isTrue();
        }
    }

    @Nested
    @DisplayName("ClienteFisico Tests")
    class ClienteFisicoTests {

        private ClienteFisico clienteFisico;

        @BeforeEach
        void setUp() {
            clienteFisico = new ClienteFisico();
            clienteFisico.setId(1L);
            clienteFisico.setTipo(TipoCliente.FISICA);
            clienteFisico.setEmail("fisico@example.com");
            clienteFisico.setNome("João Silva");
            clienteFisico.setRg("123456789");
            clienteFisico.setDataNascimento(LocalDate.of(1990, 1, 1));
        }

        @Test
        @DisplayName("Should set CPF to null when input is null")
        void testSetCpfNull() {
            clienteFisico.setCpf("12345678901");
            clienteFisico.setCpf(null);

            assertThat(clienteFisico.getCpf()).isNull();
        }

        @Test
        @DisplayName("Should strip non-digits from CPF")
        void testStripNonDigitsFromCpf() {
            clienteFisico.setCpf("123.456.789-01");

            assertThat(clienteFisico.getCpf()).isEqualTo("12345678901");
        }

        @Test
        @DisplayName("Should handle CPF with only digits")
        void testCpfWithOnlyDigits() {
            clienteFisico.setCpf("12345678901");

            assertThat(clienteFisico.getCpf()).isEqualTo("12345678901");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when copying from wrong type")
        void testCopyFromWrongType() {
            ClienteJuridico juridico = new ClienteJuridico();

            assertThatThrownBy(() -> clienteFisico.copyFrom(juridico))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Source must be an instance of ClienteFisico");
        }

        @Test
        @DisplayName("Should copy all fields from another ClienteFisico")
        void testCopyFromClienteFisico() {
            ClienteFisico source = new ClienteFisico();
            source.setNome("Maria Silva");
            source.setCpf("987.654.321-00");
            source.setRg("987654321");
            source.setDataNascimento(LocalDate.of(1995, 5, 15));

            clienteFisico.copyFrom(source);

            assertThat(clienteFisico.getNome()).isEqualTo("Maria Silva");
            assertThat(clienteFisico.getCpf()).isEqualTo("98765432100");
            assertThat(clienteFisico.getRg()).isEqualTo("987654321");
            assertThat(clienteFisico.getDataNascimento()).isEqualTo(LocalDate.of(1995, 5, 15));
        }
    }

    @Nested
    @DisplayName("ClienteJuridico Tests")
    class ClienteJuridicoTests {

        private ClienteJuridico clienteJuridico;

        @BeforeEach
        void setUp() {
            clienteJuridico = new ClienteJuridico();
            clienteJuridico.setId(1L);
            clienteJuridico.setTipo(TipoCliente.JURIDICA);
            clienteJuridico.setEmail("juridico@example.com");
            clienteJuridico.setRazaoSocial("Empresa LTDA");
            clienteJuridico.setDataCriacaoEmpresa(LocalDate.now().minusYears(2));
        }

        @Test
        @DisplayName("Should set CNPJ to null when input is null")
        void testSetCnpjNull() {
            clienteJuridico.setCnpj("12345678000190");
            clienteJuridico.setCnpj(null);

            assertThat(clienteJuridico.getCnpj()).isNull();
        }

        @Test
        @DisplayName("Should strip non-digits from CNPJ")
        void testStripNonDigitsFromCnpj() {
            clienteJuridico.setCnpj("12.345.678/0001-90");

            assertThat(clienteJuridico.getCnpj()).isEqualTo("12345678000190");
        }

        @Test
        @DisplayName("Should set Inscrição Estadual to null when input is null")
        void testSetInscricaoEstadualNull() {
            clienteJuridico.setInscricaoEstadual("123456789012");
            clienteJuridico.setInscricaoEstadual(null);

            assertThat(clienteJuridico.getInscricaoEstadual()).isNull();
        }

        @Test
        @DisplayName("Should strip non-digits from Inscrição Estadual")
        void testStripNonDigitsFromInscricaoEstadual() {
            clienteJuridico.setInscricaoEstadual("123.456.789.012");

            assertThat(clienteJuridico.getInscricaoEstadual()).isEqualTo("123456789012");
        }

        @Test
        @DisplayName("Should throw IllegalStateException when CNPJ length != 14 on persist")
        void testValidateCnpjLengthOnPersist() throws Exception {
            clienteJuridico.setCnpj("123456789");
            clienteJuridico.setInscricaoEstadual("123456789012");

            java.lang.reflect.Method validateFields = ClienteJuridico.class.getDeclaredMethod("validateFields");
            validateFields.setAccessible(true);

            assertThatThrownBy(() -> {
                try {
                    validateFields.invoke(clienteJuridico);
                } catch (java.lang.reflect.InvocationTargetException e) {
                    throw e.getCause();
                }
            })
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("CNPJ must have exactly 14 digits");
        }

        @Test
        @DisplayName("Should throw IllegalStateException when IE length < 8 on persist")
        void testValidateInscricaoEstadualTooShortOnPersist() throws Exception {
            clienteJuridico.setCnpj("12345678000190");
            clienteJuridico.setInscricaoEstadual("1234567");

            java.lang.reflect.Method validateFields = ClienteJuridico.class.getDeclaredMethod("validateFields");
            validateFields.setAccessible(true);

            assertThatThrownBy(() -> {
                try {
                    validateFields.invoke(clienteJuridico);
                } catch (java.lang.reflect.InvocationTargetException e) {
                    throw e.getCause();
                }
            })
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Inscrição Estadual must have between 8 and 14 digits");
        }

        @Test
        @DisplayName("Should throw IllegalStateException when IE length > 14 on persist")
        void testValidateInscricaoEstadualTooLongOnPersist() throws Exception {
            clienteJuridico.setCnpj("12345678000190");
            clienteJuridico.setInscricaoEstadual("123456789012345");

            java.lang.reflect.Method validateFields = ClienteJuridico.class.getDeclaredMethod("validateFields");
            validateFields.setAccessible(true);

            assertThatThrownBy(() -> {
                try {
                    validateFields.invoke(clienteJuridico);
                } catch (java.lang.reflect.InvocationTargetException e) {
                    throw e.getCause();
                }
            })
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Inscrição Estadual must have between 8 and 14 digits");
        }

        @Test
        @DisplayName("Should throw IllegalStateException when company creation date is in future")
        void testValidateFutureCreationDateOnPersist() throws Exception {
            clienteJuridico.setCnpj("12345678000190");
            clienteJuridico.setInscricaoEstadual("123456789012");
            clienteJuridico.setDataCriacaoEmpresa(LocalDate.now().plusDays(1));

            java.lang.reflect.Method validateFields = ClienteJuridico.class.getDeclaredMethod("validateFields");
            validateFields.setAccessible(true);

            assertThatThrownBy(() -> {
                try {
                    validateFields.invoke(clienteJuridico);
                } catch (java.lang.reflect.InvocationTargetException e) {
                    throw e.getCause();
                }
            })
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Data de criação da empresa não pode ser no futuro");
        }

        @Test
        @DisplayName("Should throw IllegalStateException when company less than 1 year old")
        void testValidateCompanyLessThanOneYearOnPersist() throws Exception {
            clienteJuridico.setCnpj("12345678000190");
            clienteJuridico.setInscricaoEstadual("123456789012");
            clienteJuridico.setDataCriacaoEmpresa(LocalDate.now().minusMonths(6));

            java.lang.reflect.Method validateFields = ClienteJuridico.class.getDeclaredMethod("validateFields");
            validateFields.setAccessible(true);

            assertThatThrownBy(() -> {
                try {
                    validateFields.invoke(clienteJuridico);
                } catch (java.lang.reflect.InvocationTargetException e) {
                    throw e.getCause();
                }
            })
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Empresa deve ter pelo menos 1 ano de existência");
        }

        @Test
        @DisplayName("Should pass validation with valid fields")
        void testValidateFieldsSuccess() throws Exception {
            clienteJuridico.setCnpj("12345678000190");
            clienteJuridico.setInscricaoEstadual("123456789012");
            clienteJuridico.setDataCriacaoEmpresa(LocalDate.now().minusYears(2));

            java.lang.reflect.Method validateFields = ClienteJuridico.class.getDeclaredMethod("validateFields");
            validateFields.setAccessible(true);

            assertThatCode(() -> {
                try {
                    validateFields.invoke(clienteJuridico);
                } catch (java.lang.reflect.InvocationTargetException e) {
                    throw e.getCause();
                }
            })
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when copying from wrong type")
        void testCopyFromWrongType() {
            ClienteFisico fisico = new ClienteFisico();

            assertThatThrownBy(() -> clienteJuridico.copyFrom(fisico))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Source must be an instance of ClienteJuridico");
        }

        @Test
        @DisplayName("Should copy all fields from another ClienteJuridico")
        void testCopyFromClienteJuridico() {
            ClienteJuridico source = new ClienteJuridico();
            source.setRazaoSocial("Outra Empresa LTDA");
            source.setCnpj("98.765.432/0001-00");
            source.setInscricaoEstadual("987.654.321.012");
            source.setDataCriacaoEmpresa(LocalDate.of(2020, 1, 1));

            clienteJuridico.copyFrom(source);

            assertThat(clienteJuridico.getRazaoSocial()).isEqualTo("Outra Empresa LTDA");
            assertThat(clienteJuridico.getCnpj()).isEqualTo("98765432000100");
            assertThat(clienteJuridico.getInscricaoEstadual()).isEqualTo("987654321012");
            assertThat(clienteJuridico.getDataCriacaoEmpresa()).isEqualTo(LocalDate.of(2020, 1, 1));
        }
    }
}
