package com.desafio.estagio.controller;

import com.desafio.estagio.dto.clientejuridico.*;
import com.desafio.estagio.dto.endereco.EnderecoCreateRequest;
import com.desafio.estagio.model.enums.TipoCliente;
import com.desafio.estagio.service.ClienteJuridicoService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteJuridicoController Tests")
class ClienteJuridicoControllerTest {

    @Mock
    private ClienteJuridicoService juridicoService;

    @InjectMocks
    private ClienteJuridicoController controller;

    private ClienteJuridicoResponse mockResponse;
    private ClienteJuridicoListResponse mockListResponse;
    private ClienteJuridicoCreateRequest createRequest;
    private ClienteJuridicoUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        mockResponse = ClienteJuridicoResponse.builder()
                .id(1L)
                .tipo(TipoCliente.JURIDICA)
                .cnpj("12.345.678/0001-90")
                .razaoSocial("Empresa Exemplo LTDA")
                .inscricaoEstadual("123456789")
                .email("contato@empresa.com.br")
                .dataCriacaoEmpresa(LocalDate.of(2020, 1, 15))
                .estaAtivo(true)
                .enderecos(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        mockListResponse = new ClienteJuridicoListResponse(
                1L,
                "Empresa Exemplo LTDA",
                "12.345.678/0001-90",
                "contato@empresa.com.br",
                true
        );

        createRequest = new ClienteJuridicoCreateRequest(
                "12.345.678/0001-90",
                "Empresa Exemplo LTDA",
                "123456789",
                "contato@empresa.com.br",
                LocalDate.of(2020, 1, 15),
                List.of()
        );

        updateRequest = new ClienteJuridicoUpdateRequest(
                "Empresa Exemplo LTDA Updated",
                "987654321",
                "contato.updated@empresa.com.br",
                LocalDate.of(2020, 1, 15),
                true,
                List.of()
        );
    }

    @Test
    @DisplayName("GET /v1/clientes/juridicos - Should return paginated list")
    void testGetAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClienteJuridicoListResponse> page = new PageImpl<>(List.of(mockListResponse), pageable, 1);
        when(juridicoService.findAll(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ClienteJuridicoListResponse>> response = controller.getAll(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("Empresa Exemplo LTDA", response.getBody().getContent().get(0).razaoSocial());
        assertEquals("12.345.678/0001-90", response.getBody().getContent().get(0).cnpj());
        assertEquals(1, response.getBody().getTotalElements());

        verify(juridicoService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /v1/clientes/juridicos/ativos - Should return active clients")
    void testGetAllActive() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClienteJuridicoListResponse> page = new PageImpl<>(List.of(mockListResponse), pageable, 1);
        when(juridicoService.findAllActive(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ClienteJuridicoListResponse>> response = controller.getAllActive(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertTrue(response.getBody().getContent().get(0).estaAtivo());

        verify(juridicoService, times(1)).findAllActive(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /v1/clientes/juridicos/{id} - Should return client by ID")
    void testGetById() {
        when(juridicoService.findById(1L)).thenReturn(mockResponse);

        ResponseEntity<ClienteJuridicoResponse> response = controller.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Empresa Exemplo LTDA", response.getBody().razaoSocial());
        assertEquals("12.345.678/0001-90", response.getBody().cnpj());
        assertEquals(TipoCliente.JURIDICA, response.getBody().tipo());

        verify(juridicoService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /v1/clientes/juridicos/cnpj/{cnpj} - Should return client by CNPJ")
    void testGetByCnpj() {
        when(juridicoService.findByCnpj("12.345.678/0001-90")).thenReturn(mockResponse);

        ResponseEntity<ClienteJuridicoResponse> response = controller.getByCnpj("12.345.678/0001-90");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("12.345.678/0001-90", response.getBody().cnpj());

        verify(juridicoService, times(1)).findByCnpj("12.345.678/0001-90");
    }

    @Test
    @DisplayName("GET /v1/clientes/juridicos/cnpj/{cnpj}/exists - Should verify CNPJ existence")
    void testExistsByCnpj() {
        when(juridicoService.existsByCnpj("12.345.678/0001-90")).thenReturn(true);

        ResponseEntity<Boolean> response = controller.existsByCnpj("12.345.678/0001-90");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());

        verify(juridicoService, times(1)).existsByCnpj("12.345.678/0001-90");
    }

    @Test
    @DisplayName("POST /v1/clientes/juridicos - Should create client with 201 status")
    void testCreate() {
        when(juridicoService.create(any(ClienteJuridicoCreateRequest.class))).thenReturn(mockResponse);

        ResponseEntity<ClienteJuridicoResponse> response = controller.create(createRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Empresa Exemplo LTDA", response.getBody().razaoSocial());
        assertEquals("12.345.678/0001-90", response.getBody().cnpj());

        verify(juridicoService, times(1)).create(any(ClienteJuridicoCreateRequest.class));
    }

    @Test
    @DisplayName("PUT /v1/clientes/juridicos/{id} - Should update client")
    void testUpdate() {
        when(juridicoService.update(eq(1L), any(ClienteJuridicoUpdateRequest.class))).thenReturn(mockResponse);

        ResponseEntity<ClienteJuridicoResponse> response = controller.update(1L, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());

        verify(juridicoService, times(1)).update(eq(1L), any(ClienteJuridicoUpdateRequest.class));
    }

    @Test
    @DisplayName("PATCH /v1/clientes/juridicos/{id}/ativar - Should activate with 204")
    void testActivate() {
        doNothing().when(juridicoService).activate(1L);

        ResponseEntity<Void> response = controller.activate(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(juridicoService, times(1)).activate(1L);
    }

    @Test
    @DisplayName("PATCH /v1/clientes/juridicos/{id}/inativar - Should inactivate with 204")
    void testInactivate() {
        doNothing().when(juridicoService).inactivate(1L);

        ResponseEntity<Void> response = controller.inactivate(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(juridicoService, times(1)).inactivate(1L);
    }

    @Test
    @DisplayName("DELETE /v1/clientes/juridicos/{id} - Should hard delete with 204")
    void testHardDelete() {
        doNothing().when(juridicoService).hardDelete(1L);

        ResponseEntity<Void> response = controller.hardDelete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(juridicoService, times(1)).hardDelete(1L);
    }

    @Test
    @DisplayName("GET /v1/clientes/juridicos/relatorio - Should return report data")
    void testGetReport() {
        Pageable pageable = PageRequest.of(0, 50);
        Page<ClienteJuridicoReportResponse> page = new PageImpl<>(List.of(), pageable, 0);
        when(juridicoService.findAllForReport(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ClienteJuridicoReportResponse>> response = controller.getReport(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getContent().size());

        verify(juridicoService, times(1)).findAllForReport(any(Pageable.class));
    }
}
