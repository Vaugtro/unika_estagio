package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.clientejuridico.*;
import com.desafio.estagio.dto.endereco.EnderecoCreateRequest;
import com.desafio.estagio.dto.endereco.EnderecoWithinClienteCreateRequest;
import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.exceptions.ConflictException;
import com.desafio.estagio.exceptions.ResourceNotFoundException;
import com.desafio.estagio.mapper.ClienteJuridicoMapper;
import com.desafio.estagio.model.ClienteJuridico;
import com.desafio.estagio.model.formatter.CNPJFormatter;
import com.desafio.estagio.repository.ClienteJuridicoRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteJuridicoServiceImpl Tests")
class ClienteJuridicoServiceImplTest {

    @Mock
    private ClienteJuridicoRepository repository;

    @Mock
    private ClienteJuridicoMapper mapper;

    @Mock
    private EnderecoService enderecoService;

    @InjectMocks
    private ClienteJuridicoServiceImpl service;

    private ClienteJuridico clienteJuridico;
    private ClienteJuridicoResponse clienteJuridicoResponse;
    private ClienteJuridicoCreateRequest createRequest;
    private ClienteJuridicoUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        clienteJuridico = ClienteJuridico.builder()
                .id(1L)
                .cnpj("12345678000134")
                .razaoSocial("Empresa LTDA")
                .inscricaoEstadual("123456789012")
                .email("empresa@example.com")
                .dataCriacaoEmpresa(LocalDate.of(2020, 1, 15))
                .estaAtivo(true)
                .build();

        clienteJuridicoResponse = ClienteJuridicoResponse.builder()
                .id(1L)
                .cnpj("12.345.678/0001-34")
                .razaoSocial("Empresa LTDA")
                .inscricaoEstadual("123456789012")
                .email("empresa@example.com")
                .dataCriacaoEmpresa(LocalDate.of(2020, 1, 15))
                .estaAtivo(true)
                .build();

        EnderecoCreateRequest endereco = EnderecoCreateRequest.builder()
                .logradouro("Avenida B")
                .numero(456L)
                .cep("02001-000")
                .bairro("Vila Mariana")
                .telefone("(11) 98765-4321")
                .estado("SP")
                .cidade("São Paulo")
                .principal(true)
                .complemento("Sala 100")
                .clienteId(1L)
                .build();

        createRequest = ClienteJuridicoCreateRequest.builder()
                .cnpj("12.345.678/0001-34")
                .razaoSocial("Empresa LTDA")
                .inscricaoEstadual("123456789012")
                .email("empresa@example.com")
                .dataCriacaoEmpresa(LocalDate.of(2020, 1, 15))
                .enderecos(List.of(endereco))
                .build();

        updateRequest = ClienteJuridicoUpdateRequest.builder()
                .razaoSocial("Empresa LTDA Updated")
                .inscricaoEstadual("987654321098")
                .email("empresa.updated@example.com")
                .dataCriacaoEmpresa(LocalDate.of(2020, 1, 15))
                .build();
    }

    // =====================================================
    // CREATE TESTS
    // =====================================================

    @Test
    @DisplayName("create: success with valid request")
    void testCreateSuccess() {
        // Arrange
        when(repository.existsByCnpj("12345678000134")).thenReturn(false);
        when(mapper.toEntity(any(ClienteJuridicoCreateRequest.class))).thenReturn(clienteJuridico);
        when(repository.save(any(ClienteJuridico.class))).thenReturn(clienteJuridico);
        when(mapper.toResponse(clienteJuridico)).thenReturn(clienteJuridicoResponse);

        // Act
        ClienteJuridicoResponse result = service.create(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Empresa LTDA", result.razaoSocial());
        verify(repository).existsByCnpj("12345678000134");
        verify(repository).save(any(ClienteJuridico.class));
        verify(enderecoService).create(any(EnderecoCreateRequest.class));
    }

    @Test
    @DisplayName("create: throws ConflictException when CNPJ already exists")
    void testCreateCnpjAlreadyExists() {
        // Arrange
        when(repository.existsByCnpj("12345678000134")).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictException.class, () -> service.create(createRequest));
        verify(repository).existsByCnpj("12345678000134");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("create: throws BusinessException when no enderecos provided")
    void testCreateNoEnderecos() {
        // Arrange
        ClienteJuridicoCreateRequest requestNoEnderecos = ClienteJuridicoCreateRequest.builder()
                .cnpj("12.345.678/0001-34")
                .razaoSocial("Empresa LTDA")
                .inscricaoEstadual("123456789012")
                .email("empresa@example.com")
                .dataCriacaoEmpresa(LocalDate.of(2020, 1, 15))
                .enderecos(List.of())
                .build();

        when(repository.existsByCnpj("12345678000134")).thenReturn(false);

        // Act & Assert
        assertThrows(BusinessException.class, () -> service.create(requestNoEnderecos));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("create: throws BusinessException when no principal endereco")
    void testCreateNoPrincipalEndereco() {
        // Arrange
        EnderecoCreateRequest endereco = EnderecoCreateRequest.builder()
                .logradouro("Avenida B")
                .numero(456L)
                .cep("02001-000")
                .bairro("Vila Mariana")
                .telefone("(11) 98765-4321")
                .estado("SP")
                .cidade("São Paulo")
                .principal(false)
                .complemento("Sala 100")
                .clienteId(1L)
                .build();

        ClienteJuridicoCreateRequest requestNoPrincipal = ClienteJuridicoCreateRequest.builder()
                .cnpj("12.345.678/0001-34")
                .razaoSocial("Empresa LTDA")
                .inscricaoEstadual("123456789012")
                .email("empresa@example.com")
                .dataCriacaoEmpresa(LocalDate.of(2020, 1, 15))
                .enderecos(List.of(endereco))
                .build();

        when(repository.existsByCnpj("12345678000134")).thenReturn(false);

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
        when(repository.findById(1L)).thenReturn(Optional.of(clienteJuridico));
        when(repository.save(any(ClienteJuridico.class))).thenReturn(clienteJuridico);
        when(mapper.toResponse(clienteJuridico)).thenReturn(clienteJuridicoResponse);

        // Act
        ClienteJuridicoResponse result = service.update(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(repository).findById(1L);
        verify(mapper).updateEntity(updateRequest, clienteJuridico);
        verify(repository).save(clienteJuridico);
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
        ClienteJuridico inactiveCliente = ClienteJuridico.builder()
                .id(1L)
                .cnpj("12345678000134")
                .razaoSocial("Empresa LTDA")
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
        when(repository.findById(1L)).thenReturn(Optional.of(clienteJuridico));
        when(mapper.toResponse(clienteJuridico)).thenReturn(clienteJuridicoResponse);

        // Act
        ClienteJuridicoResponse result = service.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Empresa LTDA", result.razaoSocial());
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
        ClienteJuridicoListResponse listResponse = ClienteJuridicoListResponse.builder()
                .id(1L)
                .cnpj("12.345.678/0001-34")
                .razaoSocial("Empresa LTDA")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(clienteJuridico));
        when(mapper.toListResponse(clienteJuridico)).thenReturn(listResponse);

        // Act
        ClienteJuridicoListResponse result = service.findByIdList(1L);

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
        ClienteJuridicoListResponse listResponse = ClienteJuridicoListResponse.builder()
                .id(1L)
                .cnpj("12.345.678/0001-34")
                .razaoSocial("Empresa LTDA")
                .build();

        Page<ClienteJuridico> page = new PageImpl<>(List.of(clienteJuridico), pageable, 1);
        when(repository.findByEstaAtivoTrue(pageable)).thenReturn(page);
        when(mapper.toListResponse(clienteJuridico)).thenReturn(listResponse);

        // Act
        Page<ClienteJuridicoListResponse> result = service.findAllActive(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(repository).findByEstaAtivoTrue(pageable);
    }

    // =====================================================
    // FIND ALL PAGINATED TESTS
    // =====================================================

    @Test
    @DisplayName("findAll (paginated): returns paginated clientes")
    void testFindAllPaginatedSuccess() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        ClienteJuridicoListResponse listResponse = ClienteJuridicoListResponse.builder()
                .id(1L)
                .cnpj("12.345.678/0001-34")
                .razaoSocial("Empresa LTDA")
                .build();

        Page<ClienteJuridico> page = new PageImpl<>(List.of(clienteJuridico), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.toListResponse(clienteJuridico)).thenReturn(listResponse);

        // Act
        Page<ClienteJuridicoListResponse> result = service.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findAll(pageable);
    }

    // =====================================================
    // FIND ALL NON-PAGINATED TESTS
    // =====================================================

    @Test
    @DisplayName("findAll (non-paginated): returns all clientes as list")
    void testFindAllNonPaginatedSuccess() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of(clienteJuridico));
        when(mapper.toResponse(clienteJuridico)).thenReturn(clienteJuridicoResponse);

        // Act
        List<ClienteJuridicoResponse> result = service.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        verify(repository).findAll();
    }

    // =====================================================
    // FIND ALL FOR REPORT TESTS
    // =====================================================

    @Test
    @DisplayName("findAllForReport: returns paginated report responses")
    void testFindAllForReportSuccess() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        ClienteJuridicoReportResponse reportResponse = ClienteJuridicoReportResponse.builder()
                .id(1L)
                .cnpj("12.345.678/0001-34")
                .razaoSocial("Empresa LTDA")
                .build();

        Page<ClienteJuridico> page = new PageImpl<>(List.of(clienteJuridico), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.toReportResponse(clienteJuridico)).thenReturn(reportResponse);

        // Act
        Page<ClienteJuridicoReportResponse> result = service.findAllForReport(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findAll(pageable);
    }

    // =====================================================
    // EXISTS BY CNPJ TESTS
    // =====================================================

    @Test
    @DisplayName("existsByCnpj: returns true when CNPJ exists")
    void testExistsByCnpjTrue() {
        // Arrange
        when(repository.existsByCnpj("12345678000134")).thenReturn(true);

        // Act
        boolean result = service.existsByCnpj("12.345.678/0001-34");

        // Assert
        assertTrue(result);
        verify(repository).existsByCnpj("12345678000134");
    }

    @Test
    @DisplayName("existsByCnpj: returns false when CNPJ does not exist")
    void testExistsByCnpjFalse() {
        // Arrange
        when(repository.existsByCnpj("99999999000199")).thenReturn(false);

        // Act
        boolean result = service.existsByCnpj("99.999.999/0001-99");

        // Assert
        assertFalse(result);
        verify(repository).existsByCnpj("99999999000199");
    }

    // =====================================================
    // FIND BY CNPJ TESTS
    // =====================================================

    @Test
    @DisplayName("findByCnpj: returns response when found")
    void testFindByCnpjSuccess() {
        // Arrange
        when(repository.findByCnpj("12345678000134")).thenReturn(Optional.of(clienteJuridico));
        when(mapper.toResponse(clienteJuridico)).thenReturn(clienteJuridicoResponse);

        // Act
        ClienteJuridicoResponse result = service.findByCnpj("12.345.678/0001-34");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(repository).findByCnpj("12345678000134");
    }

    @Test
    @DisplayName("findByCnpj: throws ResourceNotFoundException when not found")
    void testFindByCnpjNotFound() {
        // Arrange
        when(repository.findByCnpj("99999999000199")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.findByCnpj("99.999.999/0001-99"));
        verify(repository).findByCnpj("99999999000199");
    }
}
