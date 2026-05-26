package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.endereco.*;
import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.exceptions.ResourceNotFoundException;
import com.desafio.estagio.mapper.EnderecoMapper;
import com.desafio.estagio.model.Cliente;
import com.desafio.estagio.model.ClienteFisico;
import com.desafio.estagio.model.Endereco;
import com.desafio.estagio.repository.ClienteRepository;
import com.desafio.estagio.repository.EnderecoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnderecoServiceImpl Tests")
class EnderecoServiceImplTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private ClienteRepository<Cliente> clienteRepository;

    @Mock
    private EnderecoMapper enderecoMapper;

    @InjectMocks
    private EnderecoServiceImpl service;

    private Endereco mockEndereco;
    private Endereco mockEnderecoPrincipal;
    private Cliente mockCliente;
    private EnderecoResponse mockResponse;
    private EnderecoListResponse mockListResponse;

    @BeforeEach
    void setUp() {
        mockCliente = ClienteFisico.builder()
                .id(1L)
                .build();

        mockEndereco = Endereco.builder()
                .id(1L)
                .logradouro("Rua A")
                .numero(123L)
                .cep("12345-678")
                .bairro("Centro")
                .telefone("1199999999")
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .complemento("Apt 101")
                .cliente(mockCliente)
                .build();

        mockEnderecoPrincipal = Endereco.builder()
                .id(2L)
                .logradouro("Rua B")
                .numero(456L)
                .cep("54321-876")
                .bairro("Comercial")
                .telefone("1188888888")
                .estado("SP")
                .cidade("São Paulo")
                .principal(true)
                .complemento("Sala 200")
                .cliente(mockCliente)
                .build();

        // Add enderecos to mockCliente so real entity operations work
        mockCliente.getEnderecos().add(mockEndereco);

        mockResponse = EnderecoResponse.builder()
                .id(1L)
                .logradouro("Rua A")
                .numero(123L)
                .cep("12345-678")
                .bairro("Centro")
                .telefone("1199999999")
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .complemento("Apt 101")
                .build();

        mockListResponse = EnderecoListResponse.builder()
                .id(1L)
                .logradouro("Rua A")
                .numero(123L)
                .principal(false)
                .build();
    }

    // =====================================================
    // CREATE TESTS
    // =====================================================

    @Test
    @DisplayName("create: throws BusinessException when clienteId is null")
    void testCreateNullClienteId() {
        // Arrange
        EnderecoCreateRequest request = EnderecoCreateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("12345-678")
                .bairro("Centro")
                .telefone("1199999999")
                .estado("SP")
                .cidade("São Paulo")
                .principal(true)
                .clienteId(null)
                .build();

        // Act & Assert
        assertThrows(BusinessException.class, () -> service.create(request));
        verify(enderecoRepository, never()).save(any());
    }

    @Test
    @DisplayName("create: success with valid request")
    void testCreateSuccess() {
        // Arrange
        EnderecoCreateRequest request = EnderecoCreateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("12345-678")
                .bairro("Centro")
                .telefone("1199999999")
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .complemento("Apt 101")
                .clienteId(1L)
                .build();

        when(enderecoMapper.toEntity(request)).thenReturn(mockEndereco);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(mockCliente));
        when(enderecoRepository.countByClienteId(1L)).thenReturn(0L);
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(mockEndereco);
        when(enderecoMapper.toResponse(mockEndereco)).thenReturn(mockResponse);

        // Act
        EnderecoResponse result = service.create(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(clienteRepository).findById(1L);
        verify(enderecoRepository).save(any(Endereco.class));
    }

    @Test
    @DisplayName("create: success without telefone (telefone is optional)")
    void testCreateWithoutTelefone() {
        // Arrange
        Endereco enderecoSemTelefone = Endereco.builder()
                .id(1L)
                .logradouro("Rua A")
                .numero(123L)
                .cep("12345-678")
                .bairro("Centro")
                .telefone(null)
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .complemento("Apt 101")
                .cliente(mockCliente)
                .build();

        EnderecoResponse responseSemTelefone = EnderecoResponse.builder()
                .id(1L)
                .logradouro("Rua A")
                .numero(123L)
                .cep("12345-678")
                .bairro("Centro")
                .telefone(null)
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .complemento("Apt 101")
                .build();

        EnderecoCreateRequest request = EnderecoCreateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("12345-678")
                .bairro("Centro")
                .telefone(null)
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .complemento("Apt 101")
                .clienteId(1L)
                .build();

        when(enderecoMapper.toEntity(request)).thenReturn(enderecoSemTelefone);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(mockCliente));
        when(enderecoRepository.countByClienteId(1L)).thenReturn(1L);
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(enderecoSemTelefone);
        when(enderecoMapper.toResponse(enderecoSemTelefone)).thenReturn(responseSemTelefone);

        // Act
        EnderecoResponse result = service.create(request);

        // Assert
        assertNotNull(result);
        assertNull(result.telefone());
        verify(clienteRepository).findById(1L);
        verify(enderecoRepository).save(any(Endereco.class));
    }

    @Test
    @DisplayName("create: success for first address without telefone (first address auto-set as principal)")
    void testCreateFirstAddressWithoutTelefone() {
        // Arrange
        Endereco enderecoSemTelefone = Endereco.builder()
                .id(1L)
                .logradouro("Rua A")
                .numero(123L)
                .cep("12345-678")
                .bairro("Centro")
                .telefone(null)
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .complemento("Apt 101")
                .cliente(mockCliente)
                .build();

        EnderecoCreateRequest request = EnderecoCreateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("12345-678")
                .bairro("Centro")
                .telefone(null)
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .complemento("Apt 101")
                .clienteId(1L)
                .build();

        when(enderecoMapper.toEntity(request)).thenReturn(enderecoSemTelefone);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(mockCliente));
        when(enderecoRepository.countByClienteId(1L)).thenReturn(0L);
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(enderecoSemTelefone);
        when(enderecoMapper.toResponse(enderecoSemTelefone)).thenReturn(mockResponse);

        // Act
        service.create(request);

        // Assert
        verify(enderecoRepository).save(any(Endereco.class));
    }

    @Test
    @DisplayName("create: throws ResourceNotFoundException when cliente not found")
    void testCreateClienteNotFound() {
        // Arrange
        EnderecoCreateRequest request = EnderecoCreateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("12345-678")
                .bairro("Centro")
                .telefone("1199999999")
                .estado("SP")
                .cidade("São Paulo")
                .principal(true)
                .clienteId(999L)
                .build();

        when(enderecoMapper.toEntity(request)).thenReturn(mockEndereco);
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
        verify(enderecoRepository, never()).save(any());
    }

    @Test
    @DisplayName("create: first address auto-set as principal")
    void testCreateFirstAddressAsPrincipal() {
        // Arrange
        EnderecoCreateRequest request = EnderecoCreateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("12345-678")
                .bairro("Centro")
                .telefone("1199999999")
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .clienteId(1L)
                .build();

        when(enderecoMapper.toEntity(request)).thenReturn(mockEndereco);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(mockCliente));
        when(enderecoRepository.countByClienteId(1L)).thenReturn(0L);
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(mockEndereco);
        when(enderecoMapper.toResponse(mockEndereco)).thenReturn(mockResponse);

        // Act
        service.create(request);

        // Assert
        verify(enderecoRepository).save(any(Endereco.class));
    }

    // =====================================================
    // CREATE FOR CLIENTE TESTS
    // =====================================================

    @Test
    @DisplayName("createForCliente: success with valid request")
    void testCreateForClienteSuccess() {
        // Arrange
        EnderecoWithinClienteCreateRequest request = EnderecoWithinClienteCreateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("12345-678")
                .bairro("Centro")
                .telefone("1199999999")
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .complemento("Apt 101")
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(mockCliente));
        when(enderecoMapper.toEntity(request)).thenReturn(mockEndereco);
        when(enderecoRepository.countByClienteId(1L)).thenReturn(1L);
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(mockEndereco);
        when(enderecoMapper.toResponse(mockEndereco)).thenReturn(mockResponse);

        // Act
        EnderecoResponse result = service.createForCliente(1L, request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(clienteRepository).findById(1L);
        verify(enderecoRepository).save(any(Endereco.class));
    }

    @Test
    @DisplayName("createForCliente: throws ResourceNotFoundException when cliente not found")
    void testCreateForClienteNotFound() {
        // Arrange
        EnderecoWithinClienteCreateRequest request = EnderecoWithinClienteCreateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("12345-678")
                .bairro("Centro")
                .telefone("1199999999")
                .estado("SP")
                .cidade("São Paulo")
                .principal(true)
                .build();

        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.createForCliente(999L, request));
        verify(enderecoRepository, never()).save(any());
    }

    // =====================================================
    // FIND TESTS
    // =====================================================

    @Test
    @DisplayName("findById: returns response when found")
    void testFindByIdSuccess() {
        // Arrange
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(mockEndereco));
        when(enderecoMapper.toResponse(mockEndereco)).thenReturn(mockResponse);

        // Act
        EnderecoResponse result = service.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(enderecoRepository).findById(1L);
    }

    @Test
    @DisplayName("findById: throws ResourceNotFoundException when not found")
    void testFindByIdNotFound() {
        // Arrange
        when(enderecoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.findById(999L));
        verify(enderecoRepository).findById(999L);
    }

    @Test
    @DisplayName("findAllByClienteId: returns paginated enderecos")
    void testFindAllByClienteIdPaginated() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Endereco> page = new PageImpl<>(List.of(mockEndereco), pageable, 1);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(mockCliente));
        when(enderecoRepository.findByClienteId(1L, pageable)).thenReturn(page);
        when(enderecoMapper.toListResponse(mockEndereco)).thenReturn(mockListResponse);

        // Act
        Page<EnderecoListResponse> result = service.findAllByClienteId(1L, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(clienteRepository).findById(1L);
        verify(enderecoRepository).findByClienteId(1L, pageable);
    }

    @Test
    @DisplayName("findAllByClienteId: returns list of enderecos")
    void testFindAllByClienteIdList() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(mockCliente));
        when(enderecoRepository.findByClienteId(1L)).thenReturn(List.of(mockEndereco));
        when(enderecoMapper.toResponse(mockEndereco)).thenReturn(mockResponse);

        // Act
        List<EnderecoResponse> result = service.findAllByClienteId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(clienteRepository).findById(1L);
        verify(enderecoRepository).findByClienteId(1L);
    }

    @Test
    @DisplayName("findAllByClienteId: throws ResourceNotFoundException when cliente not found")
    void testFindAllByClienteIdClienteNotFound() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.findAllByClienteId(999L, pageable));
        verify(clienteRepository).findById(999L);
    }

    @Test
    @DisplayName("findPrincipalByClienteId: returns principal endereco when found")
    void testFindPrincipalByClienteIdSuccess() {
        // Arrange
        EnderecoResponse principalResponse = EnderecoResponse.builder()
                .id(2L)
                .logradouro("Rua B")
                .numero(456L)
                .principal(true)
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(mockCliente));
        when(enderecoRepository.findByClienteIdAndPrincipalTrue(1L)).thenReturn(Optional.of(mockEnderecoPrincipal));
        when(enderecoMapper.toResponse(mockEnderecoPrincipal)).thenReturn(principalResponse);

        // Act
        EnderecoResponse result = service.findPrincipalByClienteId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.principal());
        verify(clienteRepository).findById(1L);
        verify(enderecoRepository).findByClienteIdAndPrincipalTrue(1L);
    }

    @Test
    @DisplayName("findPrincipalByClienteId: throws ResourceNotFoundException when not found")
    void testFindPrincipalByClienteIdNotFound() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(mockCliente));
        when(enderecoRepository.findByClienteIdAndPrincipalTrue(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.findPrincipalByClienteId(1L));
        verify(clienteRepository).findById(1L);
    }

    // =====================================================
    // COUNT & VALIDATION TESTS
    // =====================================================

    @Test
    @DisplayName("countByClienteId: returns correct count")
    void testCountByClienteId() {
        // Arrange
        when(enderecoRepository.countByClienteId(1L)).thenReturn(3L);

        // Act
        long result = service.countByClienteId(1L);

        // Assert
        assertEquals(3L, result);
        verify(enderecoRepository).countByClienteId(1L);
    }

    @Test
    @DisplayName("hasAtLeastOneAddress: returns true when addresses exist")
    void testHasAtLeastOneAddressTrue() {
        // Arrange
        when(enderecoRepository.countByClienteId(1L)).thenReturn(1L);

        // Act
        boolean result = service.hasAtLeastOneAddress(1L);

        // Assert
        assertTrue(result);
        verify(enderecoRepository).countByClienteId(1L);
    }

    @Test
    @DisplayName("hasAtLeastOneAddress: returns false when no addresses")
    void testHasAtLeastOneAddressFalse() {
        // Arrange
        when(enderecoRepository.countByClienteId(1L)).thenReturn(0L);

        // Act
        boolean result = service.hasAtLeastOneAddress(1L);

        // Assert
        assertFalse(result);
        verify(enderecoRepository).countByClienteId(1L);
    }

    @Test
    @DisplayName("hasPrincipalAddress: returns true when principal exists")
    void testHasPrincipalAddressTrue() {
        // Arrange
        when(enderecoRepository.existsByClienteIdAndPrincipalTrue(1L)).thenReturn(true);

        // Act
        boolean result = service.hasPrincipalAddress(1L);

        // Assert
        assertTrue(result);
        verify(enderecoRepository).existsByClienteIdAndPrincipalTrue(1L);
    }

    @Test
    @DisplayName("hasPrincipalAddress: returns false when no principal")
    void testHasPrincipalAddressFalse() {
        // Arrange
        when(enderecoRepository.existsByClienteIdAndPrincipalTrue(1L)).thenReturn(false);

        // Act
        boolean result = service.hasPrincipalAddress(1L);

        // Assert
        assertFalse(result);
        verify(enderecoRepository).existsByClienteIdAndPrincipalTrue(1L);
    }

    // =====================================================
    // UPDATE TESTS
    // =====================================================

    @Test
    @DisplayName("update: success with valid request")
    void testUpdateSuccess() {
        // Arrange
        EnderecoUpdateRequest request = EnderecoUpdateRequest.builder()
                .logradouro("Rua A Updated")
                .numero(124L)
                .cep("12345-679")
                .bairro("Centro")
                .telefone("1199999998")
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .complemento("Apt 102")
                .build();

        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(mockEndereco));
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(mockEndereco);
        when(enderecoMapper.toResponse(mockEndereco)).thenReturn(mockResponse);

        // Act
        EnderecoResponse result = service.update(1L, request);

        // Assert
        assertNotNull(result);
        verify(enderecoRepository).findById(1L);
        verify(enderecoMapper).updateEntity(request, mockEndereco);
        verify(enderecoRepository).save(mockEndereco);
    }

    @Test
    @DisplayName("update: throws BusinessException when removing only principal address")
    void testUpdateRemoveOnlyPrincipalAddress() {
        // Arrange
        Endereco principalEndereco = Endereco.builder()
                .id(1L)
                .logradouro("Rua A")
                .numero(123L)
                .principal(true)
                .cliente(mockCliente)
                .build();

        EnderecoUpdateRequest request = EnderecoUpdateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .principal(false)
                .build();

        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(principalEndereco));
        when(enderecoRepository.countByClienteId(1L)).thenReturn(1L);

        // Act & Assert
        assertThrows(BusinessException.class, () -> service.update(1L, request));
        verify(enderecoRepository).findById(1L);
        verify(enderecoRepository, never()).save(any());
    }

    @Test
    @DisplayName("update: success setting address as principal")
    void testUpdateSetAsPrincipal() {
        // Arrange
        EnderecoUpdateRequest request = EnderecoUpdateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .principal(true)
                .build();

        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(mockEndereco));
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(mockEndereco);
        when(enderecoMapper.toResponse(mockEndereco)).thenReturn(mockResponse);

        // Act
        EnderecoResponse result = service.update(1L, request);

        // Assert
        assertNotNull(result);
        verify(enderecoRepository).findById(1L);
        verify(enderecoRepository).save(any(Endereco.class));
    }

    // =====================================================
    // SET AS PRINCIPAL TESTS
    // =====================================================

    @Test
    @DisplayName("setAsPrincipal: success demotes all, flushes, then promotes target")
    void testSetAsPrincipalSuccess() {
        // Arrange
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(mockEndereco));
        when(enderecoMapper.toResponse(mockEndereco)).thenReturn(mockResponse);

        // Act
        EnderecoResponse result = service.setAsPrincipal(1L);

        // Assert
        assertNotNull(result);
        verify(enderecoRepository).findById(1L);
        verify(enderecoRepository).flush();
        verify(enderecoRepository, never()).save(any());
        assertTrue(mockEndereco.getPrincipal());
    }

    @Test
    @DisplayName("setAsPrincipal: throws ResourceNotFoundException when not found")
    void testSetAsPrincipalNotFound() {
        // Arrange
        when(enderecoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.setAsPrincipal(999L));
        verify(enderecoRepository).findById(999L);
    }

    @Test
    @DisplayName("setAsPrincipal: demotes existing principal and promotes target")
    void testSetAsPrincipalWithExistingPrincipal() {
        // Arrange — cliente has two enderecos: mockEnderecoPrincipal (principal=true) and mockEndereco (principal=false)
        mockCliente.getEnderecos().add(mockEnderecoPrincipal);
        mockCliente.getEnderecos().add(mockEndereco);

        when(enderecoRepository.findById(2L)).thenReturn(Optional.of(mockEnderecoPrincipal));
        when(enderecoMapper.toResponse(mockEnderecoPrincipal)).thenReturn(mockResponse);

        // Act
        EnderecoResponse result = service.setAsPrincipal(2L);

        // Assert
        assertNotNull(result);
        // All addresses should have been demoted first, then target set to principal
        assertThat(mockEndereco.getPrincipal()).isFalse();
        assertThat(mockEnderecoPrincipal.getPrincipal()).isTrue();
        verify(enderecoRepository).findById(2L);
        verify(enderecoRepository).flush();
    }

    @Test
    @DisplayName("setAsPrincipal: when target is already principal, still works (demotes all and re-promotes)")
    void testSetAsPrincipalWhenAlreadyPrincipal() {
        // Arrange — target endereco is already principal
        mockEndereco.setPrincipal(true);
        mockCliente.getEnderecos().add(mockEndereco);

        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(mockEndereco));
        when(enderecoMapper.toResponse(mockEndereco)).thenReturn(mockResponse);

        // Act
        EnderecoResponse result = service.setAsPrincipal(1L);

        // Assert
        assertNotNull(result);
        // It was demoted then re-promoted, still principal
        assertThat(mockEndereco.getPrincipal()).isTrue();
        verify(enderecoRepository).findById(1L);
        verify(enderecoRepository).flush();
    }

    // =====================================================
    // DELETE TESTS
    // =====================================================

    @Test
    @DisplayName("delete: success delegates to cliente.removeEndereco")
    void testDeleteSuccess() {
        // Arrange
        // Add a second endereco so the real removeEndereco succeeds (size > 1)
        mockCliente.getEnderecos().add(mockEnderecoPrincipal);
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(mockEndereco));

        // Act
        service.delete(1L);

        // Assert
        verify(enderecoRepository).findById(1L);
        verify(enderecoRepository).delete(mockEndereco);
    }

    @Test
    @DisplayName("delete: throws BusinessException when cliente.removeEndereco fails")
    void testDeleteIllegalStateException() {
        // Arrange
        // mockCliente has only 1 endereco (from setUp), so removeEndereco throws IllegalStateException
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(mockEndereco));

        // Act & Assert
        assertThrows(BusinessException.class, () -> service.delete(1L));
        verify(enderecoRepository).findById(1L);
        verify(enderecoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("delete: throws ResourceNotFoundException when not found")
    void testDeleteNotFound() {
        // Arrange
        when(enderecoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.delete(999L));
        verify(enderecoRepository).findById(999L);
        verify(enderecoRepository, never()).delete(any());
    }

    // =====================================================
    // DELETE ALL BY CLIENTE TESTS
    // =====================================================

    @Test
    @DisplayName("deleteAllByClienteId: success deletes all enderecos")
    void testDeleteAllByClienteIdSuccess() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(mockCliente));
        when(enderecoRepository.deleteByClienteId(1L)).thenReturn(3L);

        // Act
        service.deleteAllByClienteId(1L);

        // Assert
        verify(clienteRepository).findById(1L);
        verify(enderecoRepository).deleteByClienteId(1L);
    }

    @Test
    @DisplayName("deleteAllByClienteId: throws ResourceNotFoundException when cliente not found")
    void testDeleteAllByClienteIdClienteNotFound() {
        // Arrange
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.deleteAllByClienteId(999L));
        verify(clienteRepository).findById(999L);
        verify(enderecoRepository, never()).deleteByClienteId(any());
    }
}
