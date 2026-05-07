package com.desafio.estagio;

import com.desafio.estagio.mvc.controller.EnderecoController;
import com.desafio.estagio.mvc.model.dto.EnderecoDTO;
import com.desafio.estagio.mvc.service.EnderecoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)  // No Spring context needed
class EnderecoControllerTest {

    @Mock
    private EnderecoService enderecoService;  // @Mock instead of @MockBean

    @InjectMocks
    private EnderecoController enderecoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private EnderecoDTO.Request request;
    private EnderecoDTO.Response response;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(enderecoController).build();
        objectMapper = new ObjectMapper();

        request = new EnderecoDTO.Request(
                "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", true, "Apto 42", null
        );

        response = new EnderecoDTO.Response(
                1L, "Rua das Flores", 123L, "01234-567", "Centro",
                "(11) 91234-5678", "São Paulo", "SP", "Apto 42",
                true, null, null, null
        );
    }

    @Test
    void create_ShouldReturnCreatedAddress() throws Exception {
        when(enderecoService.create(any(EnderecoDTO.Request.class))).thenReturn(response);

        mockMvc.perform(post("/api/enderecos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.logradouro").value("Rua das Flores"))
                .andExpect(jsonPath("$.principal").value(true));
    }

    @Test
    void createForCliente_ShouldReturnCreatedAddress() throws Exception {
        when(enderecoService.createForCliente(eq(1L), any(EnderecoDTO.Request.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/enderecos/cliente/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.logradouro").value("Rua das Flores"));
    }

    @Test
    void findById_ShouldReturnAddress() throws Exception {
        when(enderecoService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/enderecos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.logradouro").value("Rua das Flores"));
    }

    @Test
    void findAllByClienteId_ShouldReturnAddressList() throws Exception {
        List<EnderecoDTO.Response> responses = List.of(response);
        when(enderecoService.findAllByClienteId(1L)).thenReturn(responses);

        mockMvc.perform(get("/api/enderecos/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].logradouro").value("Rua das Flores"));
    }

    @Test
    void findAll_ShouldReturnAllAddresses() throws Exception {
        List<EnderecoDTO.Response> responses = List.of(response);
        when(enderecoService.findAll()).thenReturn(responses);

        mockMvc.perform(get("/api/enderecos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void update_ShouldReturnUpdatedAddress() throws Exception {
        when(enderecoService.update(eq(1L), any(EnderecoDTO.Request.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/enderecos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void setAsPrincipal_ShouldReturnUpdatedAddress() throws Exception {
        when(enderecoService.setAsPrincipal(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/enderecos/1/principal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.principal").value(true));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(enderecoService).delete(1L);

        mockMvc.perform(delete("/api/enderecos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findPrincipalEndereco_ShouldReturnPrincipalAddress() throws Exception {
        when(enderecoService.findPrincipalEnderecoByClienteId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/enderecos/cliente/1/principal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.principal").value(true));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        EnderecoDTO.Request invalidRequest = new EnderecoDTO.Request(
                "", null, "", "", "", "", "", null, "", null
        );

        mockMvc.perform(post("/api/enderecos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}