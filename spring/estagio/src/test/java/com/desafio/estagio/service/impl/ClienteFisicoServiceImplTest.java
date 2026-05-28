package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.clientefisico.*;
import com.desafio.estagio.dto.endereco.EnderecoWithinClienteCreateRequest;
import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.exceptions.ConflictException;
import com.desafio.estagio.exceptions.ResourceNotFoundException;
import com.desafio.estagio.mapper.ClienteFisicoMapper;
import com.desafio.estagio.model.ClienteFisico;
import com.desafio.estagio.repository.ClienteFisicoRepository;
import com.desafio.estagio.service.EnderecoService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteFisicoServiceImpl Tests")
class ClienteFisicoServiceImplTest {

    @Mock
    private ClienteFisicoRepository repository;

    @Mock
    private ClienteFisicoMapper mapper;

    @Mock
    private EnderecoService enderecoService;

    @InjectMocks
    private ClienteFisicoServiceImpl service;

    private ClienteFisico clienteFisico;
    private ClienteFisicoResponse clienteFisicoResponse;
    private ClienteFisicoCreateRequest createRequest;
    private ClienteFisicoUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        clienteFisico = ClienteFisico.builder()
                .id(1L)
                .cpf("12345678901")
                .nome("João Silva")
                .rg("123456789")
                .email("joao@example.com")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .estaAtivo(true)
                .build();

        clienteFisicoResponse = ClienteFisicoResponse.builder()
                .id(1L)
                .cpf("123.456.789-01")
                .nome("João Silva")
                .rg("123456789")
                .email("joao@example.com")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .estaAtivo(true)
                .build();

        EnderecoWithinClienteCreateRequest endereco = EnderecoWithinClienteCreateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("01001-000")
                .bairro("Centro")
                .telefone("(11) 91234-5678")
                .municipioId(3550308L)
                .principal(true)
                .complemento("Apto 42")
                .build();

        createRequest = ClienteFisicoCreateRequest.builder()
                .cpf("123.456.789-01")
                .nome("João Silva")
                .rg("123456789")
                .email("joao@example.com")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .enderecos(List.of(endereco))
                .build();

        updateRequest = ClienteFisicoUpdateRequest.builder()
                .nome("João Silva Updated")
                .email("joao.updated@example.com")
                .estaAtivo(true)
                .build();
    }

    // =====================================================
    // CREATE TESTS
    // =====================================================

    @Test
    @DisplayName("create: success with valid request")
    void testCreateSuccess() {
        // Arrange
        when(repository.existsByCpf("12345678901")).thenReturn(false);
        when(mapper.toEntity(any(ClienteFisicoCreateRequest.class))).thenReturn(clienteFisico);
        when(repository.save(any(ClienteFisico.class))).thenReturn(clienteFisico);
        when(mapper.toResponse(clienteFisico)).thenReturn(clienteFisicoResponse);

        // Act
        ClienteFisicoResponse result = service.create(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("João Silva", result.nome());
        verify(repository).existsByCpf("12345678901");
        verify(repository).save(any(ClienteFisico.class));
        verify(enderecoService).createForCliente(eq(1L), any());
    }

    @Test
    @DisplayName("create: throws ConflictException when CPF already exists")
    void testCreateCpfAlreadyExists() {
        // Arrange
        when(repository.existsByCpf("12345678901")).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictException.class, () -> service.create(createRequest));
        verify(repository).existsByCpf("12345678901");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("create: throws BusinessException when no enderecos provided")
    void testCreateNoEnderecos() {
        // Arrange
        ClienteFisicoCreateRequest requestNoEnderecos = ClienteFisicoCreateRequest.builder()
                .cpf("123.456.789-01")
                .nome("João Silva")
                .rg("123456789")
                .email("joao@example.com")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .enderecos(List.of())
                .build();

        when(repository.existsByCpf("12345678901")).thenReturn(false);

        // Act & Assert
        assertThrows(BusinessException.class, () -> service.create(requestNoEnderecos));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("create: throws BusinessException when no principal endereco")
    void testCreateNoPrincipalEndereco() {
        // Arrange
        EnderecoWithinClienteCreateRequest endereco = EnderecoWithinClienteCreateRequest.builder()
                .logradouro("Rua A")
                .numero(123L)
                .cep("01001-000")
                .bairro("Centro")
                .telefone("(11) 91234-5678")
                .municipioId(3550308L)
                .principal(false)
                .complemento("Apto 42")
                .build();

        ClienteFisicoCreateRequest requestNoPrincipal = ClienteFisicoCreateRequest.builder()
                .cpf("123.456.789-01")
                .nome("João Silva")
                .rg("123456789")
                .email("joao@example.com")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .enderecos(List.of(endereco))
                .build();

        when(repository.existsByCpf("12345678901")).thenReturn(false);

        // Act & Assert
        assertThrows(BusinessException.class, () -> service.create(requestNoPrincipal));
        verify(repository, never()).save(any());
    }

    // =====================================================
    // UPDATE TESTS
    // =====================================================

    @Test
    @DisplayName("update: success with valid request")
    void testUpdateSuccess() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(clienteFisico));
        when(repository.save(any(ClienteFisico.class))).thenReturn(clienteFisico);
        when(mapper.toResponse(clienteFisico)).thenReturn(clienteFisicoResponse);

        // Act
        ClienteFisicoResponse result = service.update(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(repository).findById(1L);
        verify(mapper).updateEntity(updateRequest, clienteFisico);
        verify(repository).save(clienteFisico);
    }

    @Test
    @DisplayName("update: throws ResourceNotFoundException when not found")
    void testUpdateNotFound() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.update(999L, updateRequest));
        verify(repository).findById(999L);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update: throws BusinessException when cliente is inactive")
    void testUpdateInactiveCliente() {
        // Arrange
        ClienteFisico inactiveCliente = ClienteFisico.builder()
                .id(1L)
                .cpf("12345678901")
                .nome("João Silva")
                .estaAtivo(false)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(inactiveCliente));

        // Act & Assert
        assertThrows(BusinessException.class, () -> service.update(1L, updateRequest));
        verify(repository).findById(1L);
        verify(repository, never()).save(any());
    }

    // =====================================================
    // FIND BY ID TESTS
    // =====================================================

    @Test
    @DisplayName("findById: returns response when found")
    void testFindByIdSuccess() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(clienteFisico));
        when(mapper.toResponse(clienteFisico)).thenReturn(clienteFisicoResponse);

        // Act
        ClienteFisicoResponse result = service.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("João Silva", result.nome());
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("findById: throws ResourceNotFoundException when not found")
    void testFindByIdNotFound() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.findById(999L));
        verify(repository).findById(999L);
    }

    // =====================================================
    // FIND BY ID LIST TESTS
    // =====================================================

    @Test
    @DisplayName("findByIdList: returns list response when found")
    void testFindByIdListSuccess() {
        // Arrange
        ClienteFisicoListResponse listResponse = ClienteFisicoListResponse.builder()
                .id(1L)
                .cpf("123.456.789-01")
                .nome("João Silva")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(clienteFisico));
        when(mapper.toListResponse(clienteFisico)).thenReturn(listResponse);

        // Act
        ClienteFisicoListResponse result = service.findByIdList(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(repository).findById(1L);
    }

    // =====================================================
    // FIND ALL ACTIVE TESTS
    // =====================================================

    @Test
    @DisplayName("findAllActive: returns paginated active clientes")
    void testFindAllActiveSuccess() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        ClienteFisicoListResponse listResponse = ClienteFisicoListResponse.builder()
                .id(1L)
                .cpf("123.456.789-01")
                .nome("João Silva")
                .build();

        Page<ClienteFisico> page = new PageImpl<>(List.of(clienteFisico), pageable, 1);
        when(repository.findByEstaAtivoTrue(pageable)).thenReturn(page);
        when(mapper.toListResponse(clienteFisico)).thenReturn(listResponse);

        // Act
        Page<ClienteFisicoListResponse> result = service.findAllActive(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(repository).findByEstaAtivoTrue(pageable);
    }

    // =====================================================
    // FIND ALL TESTS
    // =====================================================

    @Test
    @DisplayName("findAll: returns paginated clientes")
    void testFindAllSuccess() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        ClienteFisicoListResponse listResponse = ClienteFisicoListResponse.builder()
                .id(1L)
                .cpf("123.456.789-01")
                .nome("João Silva")
                .build();

        Page<ClienteFisico> page = new PageImpl<>(List.of(clienteFisico), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.toListResponse(clienteFisico)).thenReturn(listResponse);

        // Act
        Page<ClienteFisicoListResponse> result = service.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findAll(pageable);
    }

    // =====================================================
    // FIND ALL FOR REPORT TESTS
    // =====================================================

    @Test
    @DisplayName("findAllForReport: returns paginated report responses")
    void testFindAllForReportSuccess() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        ClienteFisicoReportResponse reportResponse = ClienteFisicoReportResponse.builder()
                .id(1L)
                .cpf("123.456.789-01")
                .nome("João Silva")
                .build();

        Page<ClienteFisico> page = new PageImpl<>(List.of(clienteFisico), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.toReportResponse(clienteFisico)).thenReturn(reportResponse);

        // Act
        Page<ClienteFisicoReportResponse> result = service.findAllForReport(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findAll(pageable);
    }

    // =====================================================
    // EXISTS BY CPF TESTS
    // =====================================================

    @Test
    @DisplayName("existsByCpf: returns true when CPF exists")
    void testExistsByCpfTrue() {
        // Arrange
        when(repository.existsByCpf("12345678901")).thenReturn(true);

        // Act
        boolean result = service.existsByCpf("12345678901");

        // Assert
        assertTrue(result);
        verify(repository).existsByCpf("12345678901");
    }

    @Test
    @DisplayName("existsByCpf: returns false when CPF does not exist")
    void testExistsByCpfFalse() {
        // Arrange
        when(repository.existsByCpf("99999999999")).thenReturn(false);

        // Act
        boolean result = service.existsByCpf("99999999999");

        // Assert
        assertFalse(result);
        verify(repository).existsByCpf("99999999999");
    }

    // =====================================================
    // FIND BY CPF TESTS
    // =====================================================

    @Test
    @DisplayName("findByCpf: returns response when found")
    void testFindByCpfSuccess() {
        // Arrange
        when(repository.findByCpf("12345678901")).thenReturn(Optional.of(clienteFisico));
        when(mapper.toResponse(clienteFisico)).thenReturn(clienteFisicoResponse);

        // Act
        ClienteFisicoResponse result = service.findByCpf("12345678901");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(repository).findByCpf("12345678901");
    }

    @Test
    @DisplayName("findByCpf: throws ResourceNotFoundException when not found")
    void testFindByCpfNotFound() {
        // Arrange
        when(repository.findByCpf("99999999999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.findByCpf("99999999999"));
        verify(repository).findByCpf("99999999999");
    }
}
