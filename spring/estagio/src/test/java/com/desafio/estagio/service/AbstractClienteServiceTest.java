package com.desafio.estagio.service;

import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.exceptions.ResourceNotFoundException;
import com.desafio.estagio.model.Cliente;
import com.desafio.estagio.model.ClienteFisico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AbstractClienteService Tests")
class AbstractClienteServiceTest {

    @Mock
    private JpaRepository<Cliente, Long> repository;

    private TestClienteService service;

    @BeforeEach
    void setUp() {
        service = new TestClienteService(repository);
    }

    private static class TestClienteService extends AbstractClienteService<Cliente, JpaRepository<Cliente, Long>> {
        public TestClienteService(JpaRepository<Cliente, Long> repository) {
            super(repository);
        }

        @Override
        protected String getEntityName() {
            return "TestCliente";
        }
    }

    /**
     * Test builder for Cliente to simplify test setup.
     */
    private static class ClienteTestBuilder {
        private Long id;
        private Boolean estaAtivo = true;

        ClienteTestBuilder id(Long id) {
            this.id = id;
            return this;
        }

        ClienteTestBuilder estaAtivo(Boolean estaAtivo) {
            this.estaAtivo = estaAtivo;
            return this;
        }

        Cliente build() {
            Cliente cliente = new ClienteFisicoTestImpl();
            cliente.setId(id);
            cliente.setEstaAtivo(estaAtivo);
            return cliente;
        }
    }

    /**
     * Concrete implementation of Cliente for testing (since Cliente is abstract).
     */
    private static class ClienteFisicoTestImpl extends ClienteFisico {
    }

    @Nested
    @DisplayName("findModelById")
    class FindModelByIdTests {

        @Test
        @DisplayName("should return entity when found")
        void shouldReturnEntityWhenFound() {
            // Arrange
            Long id = 1L;
            Cliente cliente = new ClienteTestBuilder().id(id).build();
            when(repository.findById(id)).thenReturn(Optional.of(cliente));

            // Act
            Cliente result = service.findModelById(id);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(id);
            verify(repository).findById(id);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowResourceNotFoundExceptionWhenNotFound() {
            // Arrange
            Long id = 999L;
            when(repository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.findModelById(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("TestCliente não encontrado com o ID: 999");
            verify(repository).findById(id);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException with correct message format")
        void shouldThrowResourceNotFoundExceptionWithCorrectMessageFormat() {
            // Arrange
            Long id = 42L;
            when(repository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.findModelById(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("TestCliente não encontrado com o ID: 42");
        }
    }

    @Nested
    @DisplayName("ensureIsActive")
    class EnsureIsActiveTests {

        @Test
        @DisplayName("should not throw exception when model is active")
        void shouldNotThrowExceptionWhenModelIsActive() {
            // Arrange
            Cliente cliente = new ClienteTestBuilder().estaAtivo(true).build();

            // Act & Assert
            assertThatCode(() -> service.ensureIsActive(cliente))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should throw BusinessException when model is inactive")
        void shouldThrowBusinessExceptionWhenModelIsInactive() {
            // Arrange
            Cliente cliente = new ClienteTestBuilder().estaAtivo(false).build();

            // Act & Assert
            assertThatThrownBy(() -> service.ensureIsActive(cliente))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Operação não permitida: O cliente está inativo.");
        }

        @Test
        @DisplayName("should not throw when model has null estaAtivo (treated as not explicitly inactive)")
        void shouldNotThrowWhenModelHasNullEstaAtivo() {
            // Arrange
            Cliente cliente = new ClienteTestBuilder().estaAtivo(null).build();

            // Act & Assert
            assertThatCode(() -> service.ensureIsActive(cliente))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("activate")
    class ActivateTests {

        @Test
        @DisplayName("should activate inactive model")
        void shouldActivateInactiveModel() {
            // Arrange
            Long id = 1L;
            Cliente cliente = new ClienteTestBuilder().id(id).estaAtivo(false).build();
            when(repository.findById(id)).thenReturn(Optional.of(cliente));
            when(repository.save(any(Cliente.class))).thenReturn(cliente);

            // Act
            service.activate(id);

            // Assert
            assertThat(cliente.getEstaAtivo()).isTrue();
            verify(repository).findById(id);
            verify(repository).save(cliente);
        }

        @Test
        @DisplayName("should throw BusinessException when already active")
        void shouldThrowBusinessExceptionWhenAlreadyActive() {
            // Arrange
            Long id = 1L;
            Cliente cliente = new ClienteTestBuilder().id(id).estaAtivo(true).build();
            when(repository.findById(id)).thenReturn(Optional.of(cliente));

            // Act & Assert
            assertThatThrownBy(() -> service.activate(id))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Este cliente já está ativo.");
            verify(repository).findById(id);
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when model not found")
        void shouldThrowResourceNotFoundExceptionWhenModelNotFound() {
            // Arrange
            Long id = 999L;
            when(repository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.activate(id))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(repository).findById(id);
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("inactivate")
    class InactivateTests {

        @Test
        @DisplayName("should inactivate active model")
        void shouldInactivateActiveModel() {
            // Arrange
            Long id = 1L;
            Cliente cliente = new ClienteTestBuilder().id(id).estaAtivo(true).build();
            when(repository.findById(id)).thenReturn(Optional.of(cliente));
            when(repository.save(any(Cliente.class))).thenReturn(cliente);

            // Act
            service.inactivate(id);

            // Assert
            assertThat(cliente.getEstaAtivo()).isFalse();
            verify(repository).findById(id);
            verify(repository).save(cliente);
        }

        @Test
        @DisplayName("should throw BusinessException when already inactive")
        void shouldThrowBusinessExceptionWhenAlreadyInactive() {
            // Arrange
            Long id = 1L;
            Cliente cliente = new ClienteTestBuilder().id(id).estaAtivo(false).build();
            when(repository.findById(id)).thenReturn(Optional.of(cliente));

            // Act & Assert
            assertThatThrownBy(() -> service.inactivate(id))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Este cliente já está inativo.");
            verify(repository).findById(id);
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when model not found")
        void shouldThrowResourceNotFoundExceptionWhenModelNotFound() {
            // Arrange
            Long id = 999L;
            when(repository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.inactivate(id))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(repository).findById(id);
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTests {

        @Test
        @DisplayName("should delegate to inactivate")
        void shouldDelegateToInactivate() {
            // Arrange
            Long id = 1L;
            Cliente cliente = new ClienteTestBuilder().id(id).estaAtivo(true).build();
            when(repository.findById(id)).thenReturn(Optional.of(cliente));
            when(repository.save(any(Cliente.class))).thenReturn(cliente);

            // Act
            service.delete(id);

            // Assert
            assertThat(cliente.getEstaAtivo()).isFalse();
            verify(repository).findById(id);
            verify(repository).save(cliente);
        }

        @Test
        @DisplayName("should throw BusinessException when already inactive")
        void shouldThrowBusinessExceptionWhenAlreadyInactive() {
            // Arrange
            Long id = 1L;
            Cliente cliente = new ClienteTestBuilder().id(id).estaAtivo(false).build();
            when(repository.findById(id)).thenReturn(Optional.of(cliente));

            // Act & Assert
            assertThatThrownBy(() -> service.delete(id))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Este cliente já está inativo.");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when model not found")
        void shouldThrowResourceNotFoundExceptionWhenModelNotFound() {
            // Arrange
            Long id = 999L;
            when(repository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.delete(id))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("hardDelete")
    class HardDeleteTests {

        @Test
        @DisplayName("should find and delete model")
        void shouldFindAndDeleteModel() {
            // Arrange
            Long id = 1L;
            Cliente cliente = new ClienteTestBuilder().id(id).build();
            when(repository.findById(id)).thenReturn(Optional.of(cliente));

            // Act
            service.hardDelete(id);

            // Assert
            verify(repository).findById(id);
            verify(repository).delete(cliente);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when model not found")
        void shouldThrowResourceNotFoundExceptionWhenModelNotFound() {
            // Arrange
            Long id = 999L;
            when(repository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.hardDelete(id))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(repository, never()).delete(any());
        }

        @Test
        @DisplayName("should call repository delete with correct model")
        void shouldCallRepositoryDeleteWithCorrectModel() {
            // Arrange
            Long id = 42L;
            Cliente cliente = new ClienteTestBuilder().id(id).build();
            when(repository.findById(id)).thenReturn(Optional.of(cliente));

            // Act
            service.hardDelete(id);

            // Assert
            verify(repository).delete(cliente);
        }
    }

    @Nested
    @DisplayName("count")
    class CountTests {

        @Test
        @DisplayName("should return repository count")
        void shouldReturnRepositoryCount() {
            // Arrange
            long expectedCount = 42L;
            when(repository.count()).thenReturn(expectedCount);

            // Act
            long result = service.count();

            // Assert
            assertThat(result).isEqualTo(expectedCount);
            verify(repository).count();
        }

        @Test
        @DisplayName("should return zero when no entities exist")
        void shouldReturnZeroWhenNoEntitiesExist() {
            // Arrange
            when(repository.count()).thenReturn(0L);

            // Act
            long result = service.count();

            // Assert
            assertThat(result).isZero();
            verify(repository).count();
        }

        @Test
        @DisplayName("should return large count")
        void shouldReturnLargeCount() {
            // Arrange
            long expectedCount = 1_000_000L;
            when(repository.count()).thenReturn(expectedCount);

            // Act
            long result = service.count();

            // Assert
            assertThat(result).isEqualTo(expectedCount);
        }
    }
}
