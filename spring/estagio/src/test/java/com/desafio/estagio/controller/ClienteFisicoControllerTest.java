package com.desafio.estagio.controller;

import com.desafio.estagio.dto.clientefisico.*;
import com.desafio.estagio.dto.endereco.EnderecoWithinClienteCreateRequest;
import com.desafio.estagio.model.enums.TipoCliente;
import com.desafio.estagio.service.ClienteFisicoService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteFisicoController Tests")
class ClienteFisicoControllerTest {

    @Mock
    private ClienteFisicoService fisicoService;

    @InjectMocks
    private ClienteFisicoController controller;

    private ClienteFisicoResponse mockResponse;
    private ClienteFisicoListResponse mockListResponse;
    private ClienteFisicoReportResponse mockReportResponse;
    private ClienteFisicoCreateRequest createRequest;
    private ClienteFisicoUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        mockResponse = ClienteFisicoResponse.builder()
                .id(1L)
                .tipo(TipoCliente.FISICA)
                .cpf("123.456.789-01")
                .nome("João Silva")
                .rg("123456789")
                .email("joao@example.com")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .estaAtivo(true)
                .enderecos(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        mockListResponse = new ClienteFisicoListResponse(
                1L,
                "João Silva",
                "123.456.789-01",
                "joao@example.com",
                true
        );

        mockReportResponse = ClienteFisicoReportResponse.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("123.456.789-01")
                .rg("123456789")
                .email("joao@example.com")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .estaAtivo(true)
                .createdAt(LocalDate.now())
                .build();

        createRequest = new ClienteFisicoCreateRequest(
                "123.456.789-01",
                "João Silva",
                "123456789",
                "joao@example.com",
                LocalDate.of(1990, 5, 15),
                List.of(new EnderecoWithinClienteCreateRequest(
                        "Rua A",
                        (long) 123,
                        "12345-678",
                        "Centro",
                        "(11) 99999-9999",
                        3550308L,
                        true,
                        null
                ))
        );

        updateRequest = new ClienteFisicoUpdateRequest(
                "João Silva Updated",
                "joao.updated@example.com",
                true
        );
    }

    @Test
    @DisplayName("GET /v1/clientes/fisicos - Should return paginated list of all clients")
    void testGetAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClienteFisicoListResponse> page = new PageImpl<>(List.of(mockListResponse), pageable, 1);

        when(fisicoService.findAll(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ClienteFisicoListResponse>> response = controller.getAll(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("João Silva", response.getBody().getContent().get(0).nome());
        assertEquals("123.456.789-01", response.getBody().getContent().get(0).cpf());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(0, response.getBody().getNumber());

        verify(fisicoService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /v1/clientes/fisicos/ativos - Should return paginated list of active clients")
    void testGetAllActive() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClienteFisicoListResponse> page = new PageImpl<>(List.of(mockListResponse), pageable, 1);

        when(fisicoService.findAllActive(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ClienteFisicoListResponse>> response = controller.getAllActive(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertTrue(response.getBody().getContent().get(0).estaAtivo());
        assertEquals(1, response.getBody().getTotalElements());

        verify(fisicoService, times(1)).findAllActive(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /v1/clientes/fisicos/{id} - Should return client by ID")
    void testGetById() {
        when(fisicoService.findById(1L)).thenReturn(mockResponse);

        ResponseEntity<ClienteFisicoResponse> response = controller.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("João Silva", response.getBody().nome());
        assertEquals("123.456.789-01", response.getBody().cpf());
        assertEquals(TipoCliente.FISICA, response.getBody().tipo());
        assertTrue(response.getBody().estaAtivo());

        verify(fisicoService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /v1/clientes/fisicos/cpf/{cpf} - Should return client by CPF")
    void testGetByCpf() {
        when(fisicoService.findByCpf("123.456.789-01")).thenReturn(mockResponse);

        ResponseEntity<ClienteFisicoResponse> response = controller.getByCpf("123.456.789-01");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("123.456.789-01", response.getBody().cpf());
        assertEquals("João Silva", response.getBody().nome());

        verify(fisicoService, times(1)).findByCpf("123.456.789-01");
    }

    @Test
    @DisplayName("GET /v1/clientes/fisicos/cpf/{cpf}/exists - Should verify CPF existence")
    void testExistsByCpf() {
        when(fisicoService.existsByCpf("123.456.789-01")).thenReturn(true);

        ResponseEntity<Boolean> response = controller.existsByCpf("123.456.789-01");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());

        verify(fisicoService, times(1)).existsByCpf("123.456.789-01");
    }

    @Test
    @DisplayName("GET /v1/clientes/fisicos/cpf/{cpf}/exists - Should return false when CPF not exists")
    void testExistsByCpfNotFound() {
        when(fisicoService.existsByCpf("999.999.999-99")).thenReturn(false);

        ResponseEntity<Boolean> response = controller.existsByCpf("999.999.999-99");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());

        verify(fisicoService, times(1)).existsByCpf("999.999.999-99");
    }

    @Test
    @DisplayName("POST /v1/clientes/fisicos - Should create new client with 201 status")
    void testCreate() {
        when(fisicoService.create(any(ClienteFisicoCreateRequest.class))).thenReturn(mockResponse);

        ResponseEntity<ClienteFisicoResponse> response = controller.create(createRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("João Silva", response.getBody().nome());
        assertEquals("123.456.789-01", response.getBody().cpf());
        assertTrue(response.getBody().estaAtivo());

        verify(fisicoService, times(1)).create(any(ClienteFisicoCreateRequest.class));
    }

    @Test
    @DisplayName("PUT /v1/clientes/fisicos/{id} - Should update client")
    void testUpdate() {
        when(fisicoService.update(eq(1L), any(ClienteFisicoUpdateRequest.class))).thenReturn(mockResponse);

        ResponseEntity<ClienteFisicoResponse> response = controller.update(1L, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("João Silva", response.getBody().nome());

        verify(fisicoService, times(1)).update(eq(1L), any(ClienteFisicoUpdateRequest.class));
    }

    @Test
    @DisplayName("PATCH /v1/clientes/fisicos/{id}/ativar - Should activate client with 204 status")
    void testActivate() {
        doNothing().when(fisicoService).activate(1L);

        ResponseEntity<Void> response = controller.activate(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(fisicoService, times(1)).activate(1L);
    }

    @Test
    @DisplayName("PATCH /v1/clientes/fisicos/{id}/inativar - Should inactivate client with 204 status")
    void testInactivate() {
        doNothing().when(fisicoService).inactivate(1L);

        ResponseEntity<Void> response = controller.inactivate(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(fisicoService, times(1)).inactivate(1L);
    }

    @Test
    @DisplayName("DELETE /v1/clientes/fisicos/{id} - Should soft delete client with 204 status")
    void testSoftDelete() {
        doNothing().when(fisicoService).delete(1L);

        ResponseEntity<Void> response = controller.softDelete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(fisicoService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("DELETE /v1/clientes/fisicos/{id}/permanent - Should hard delete client with 204 status")
    void testHardDelete() {
        doNothing().when(fisicoService).hardDelete(1L);

        ResponseEntity<Void> response = controller.hardDelete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(fisicoService, times(1)).hardDelete(1L);
    }

    @Test
    @DisplayName("GET /v1/clientes/fisicos/relatorio - Should return report data")
    void testGetReport() {
        Pageable pageable = PageRequest.of(0, 50);
        Page<ClienteFisicoReportResponse> page = new PageImpl<>(List.of(mockReportResponse), pageable, 1);

        when(fisicoService.findAllForReport(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ClienteFisicoReportResponse>> response = controller.getReport(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(1L, response.getBody().getContent().get(0).id());
        assertEquals("João Silva", response.getBody().getContent().get(0).nome());
        assertEquals("123.456.789-01", response.getBody().getContent().get(0).cpf());
        assertEquals(1, response.getBody().getTotalElements());

        verify(fisicoService, times(1)).findAllForReport(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /v1/clientes/fisicos - Should pass pageable parameters to service")
    void testGetAllWithPagination() {
        Pageable pageable = PageRequest.of(1, 20);
        Page<ClienteFisicoListResponse> page = new PageImpl<>(List.of(), pageable, 0);

        when(fisicoService.findAll(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ClienteFisicoListResponse>> response = controller.getAll(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getNumber());
        assertEquals(20, response.getBody().getSize());
        assertEquals(0, response.getBody().getTotalElements());

        verify(fisicoService, times(1)).findAll(any(Pageable.class));
    }
}
