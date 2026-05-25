package com.desafio.estagio.controller;

import com.desafio.estagio.dto.endereco.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnderecoController Tests")
class EnderecoControllerTest {

    @Mock
    private EnderecoService enderecoService;

    @InjectMocks
    private EnderecoController controller;

    private EnderecoResponse mockResponse;
    private EnderecoListResponse mockListResponse;
    private EnderecoCreateRequest createRequest;
    private EnderecoUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        mockResponse = EnderecoResponse.builder()
                .id(1L)
                .logradouro("Rua das Flores")
                .numero(123L)
                .cep("01001-000")
                .bairro("Centro")
                .cidade("São Paulo")
                .estado("SP")
                .telefone("(11) 91234-5678")
                .complemento("Apto 42")
                .principal(true)
                .clienteId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        mockListResponse = EnderecoListResponse.builder()
                .id(1L)
                .logradouro("Rua das Flores")
                .numero(123L)
                .cep("01001-000")
                .bairro("Centro")
                .cidade("São Paulo")
                .estado("SP")
                .principal(true)
                .build();

        createRequest = new EnderecoCreateRequest(
                "Rua das Flores",
                123L,
                "01001-000",
                "Centro",
                "(11) 91234-5678",
                "SP",
                "São Paulo",
                true,
                null,
                1L
        );

        updateRequest = new EnderecoUpdateRequest(
                "Rua das Flores Updated",
                456L,
                "01002-000",
                "Bairro Novo",
                "(11) 98765-4321",
                "RJ",
                "Rio de Janeiro",
                false,
                "Casa"
        );
    }

    @Test
    @DisplayName("POST /v1/enderecos - Should create with 201")
    void testCreate() {
        when(enderecoService.create(any(EnderecoCreateRequest.class))).thenReturn(mockResponse);

        ResponseEntity<EnderecoResponse> response = controller.create(createRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Rua das Flores", response.getBody().logradouro());
        assertEquals("01001-000", response.getBody().cep());
        assertTrue(response.getBody().principal());

        verify(enderecoService, times(1)).create(any(EnderecoCreateRequest.class));
    }

    @Test
    @DisplayName("POST /v1/enderecos/clientes/{clienteId} - Should create for cliente with 201")
    void testCreateForCliente() {
        var withinRequest = new EnderecoWithinClienteCreateRequest(
                "Rua das Flores",
                123L,
                "01001-000",
                "Centro",
                "(11) 91234-5678",
                "SP",
                "São Paulo",
                true,
                null
        );

        when(enderecoService.createForCliente(eq(1L), any(EnderecoWithinClienteCreateRequest.class)))
                .thenReturn(mockResponse);

        ResponseEntity<EnderecoResponse> response = controller.createForCliente(1L, withinRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());

        verify(enderecoService, times(1))
                .createForCliente(eq(1L), any(EnderecoWithinClienteCreateRequest.class));
    }

    @Test
    @DisplayName("GET /v1/enderecos/{id} - Should return by ID")
    void testFindById() {
        when(enderecoService.findById(1L)).thenReturn(mockResponse);

        ResponseEntity<EnderecoResponse> response = controller.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Rua das Flores", response.getBody().logradouro());

        verify(enderecoService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /v1/enderecos/clientes/{clienteId} - Should return paginated list")
    void testFindAllByClienteId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<EnderecoListResponse> page = new PageImpl<>(List.of(mockListResponse), pageable, 1);
        when(enderecoService.findAllByClienteId(eq(1L), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<EnderecoListResponse>> response = controller.findAllByClienteId(1L, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("Rua das Flores", response.getBody().getContent().get(0).logradouro());

        verify(enderecoService, times(1)).findAllByClienteId(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /v1/enderecos/clientes/{clienteId}/principal - Should return principal")
    void testFindPrincipalByClienteId() {
        when(enderecoService.findPrincipalByClienteId(1L)).thenReturn(mockResponse);

        ResponseEntity<EnderecoResponse> response = controller.findPrincipalByClienteId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().principal());

        verify(enderecoService, times(1)).findPrincipalByClienteId(1L);
    }

    @Test
    @DisplayName("GET /v1/enderecos/clientes/{clienteId}/count - Should return count")
    void testCountByClienteId() {
        when(enderecoService.countByClienteId(1L)).thenReturn(3L);

        ResponseEntity<Long> response = controller.countByClienteId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3L, response.getBody());

        verify(enderecoService, times(1)).countByClienteId(1L);
    }

    @Test
    @DisplayName("PUT /v1/enderecos/{id} - Should update")
    void testUpdate() {
        when(enderecoService.update(eq(1L), any(EnderecoUpdateRequest.class))).thenReturn(mockResponse);

        ResponseEntity<EnderecoResponse> response = controller.update(1L, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());

        verify(enderecoService, times(1)).update(eq(1L), any(EnderecoUpdateRequest.class));
    }

    @Test
    @DisplayName("PATCH /v1/enderecos/{id}/principal - Should set as principal")
    void testSetAsPrincipal() {
        when(enderecoService.setAsPrincipal(1L)).thenReturn(mockResponse);

        ResponseEntity<EnderecoResponse> response = controller.setAsPrincipal(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertTrue(response.getBody().principal());

        verify(enderecoService, times(1)).setAsPrincipal(1L);
    }

    @Test
    @DisplayName("DELETE /v1/enderecos/{id} - Should delete with 204")
    void testDelete() {
        doNothing().when(enderecoService).delete(1L);

        ResponseEntity<Void> response = controller.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(enderecoService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("DELETE /v1/enderecos/clientes/{clienteId} - Should delete all with 204")
    void testDeleteAllByClienteId() {
        doNothing().when(enderecoService).deleteAllByClienteId(1L);

        ResponseEntity<Void> response = controller.deleteAllByClienteId(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(enderecoService, times(1)).deleteAllByClienteId(1L);
    }

    @Test
    @DisplayName("GET /v1/enderecos/clientes/{clienteId}/has-addresses - Should return boolean")
    void testHasAtLeastOneAddress() {
        when(enderecoService.hasAtLeastOneAddress(1L)).thenReturn(true);

        ResponseEntity<Boolean> response = controller.hasAtLeastOneAddress(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());

        verify(enderecoService, times(1)).hasAtLeastOneAddress(1L);
    }

    @Test
    @DisplayName("GET /v1/enderecos/clientes/{clienteId}/has-principal - Should return boolean")
    void testHasPrincipalAddress() {
        when(enderecoService.hasPrincipalAddress(1L)).thenReturn(true);

        ResponseEntity<Boolean> response = controller.hasPrincipalAddress(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());

        verify(enderecoService, times(1)).hasPrincipalAddress(1L);
    }
}
