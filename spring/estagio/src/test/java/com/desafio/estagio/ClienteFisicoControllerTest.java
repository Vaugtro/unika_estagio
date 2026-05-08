package com.desafio.estagio;

import com.desafio.estagio.controller.ClienteController;
import com.desafio.estagio.dto.ClienteFisicoDTO;
import com.desafio.estagio.dto.EnderecoDTO;
import com.desafio.estagio.model.enums.TipoCliente;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ClienteFisicoControllerTest {

    @Mock
    private ClienteFisicoService fisicoService;

    @Mock
    private ClienteJuridicoService juridicoService;

    @InjectMocks
    private ClienteController clienteController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ClienteFisicoDTO.Request fisicoRequest;
    private ClienteFisicoDTO.Response fisicoResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(clienteController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        EnderecoDTO.Request enderecoRequest = new EnderecoDTO.Request(
                "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", true, "Apto 42", null
        );

        fisicoRequest = new ClienteFisicoDTO.Request(
                TipoCliente.FISICA,
                "joao.silva@email.com",
                "743.688.910-90",
                "João Silva",
                "123456789",
                true,
                LocalDate.of(1990, 1, 15),
                List.of(enderecoRequest)
        );

        fisicoResponse = new ClienteFisicoDTO.Response(
                1L,
                TipoCliente.FISICA,
                "joao.silva@email.com",
                "743.688.910-90",
                "João Silva",
                "123456789",
                true,
                LocalDate.of(1990, 1, 15),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void createFisico_ShouldReturnCreatedClient() throws Exception {
        when(fisicoService.create(any(ClienteFisicoDTO.Request.class)))
                .thenReturn(fisicoResponse);

        mockMvc.perform(post("/clientes/fisicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fisicoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cpf").value("743.688.910-90"));
    }

    @Test
    void getFisicoById_ShouldReturnClient() throws Exception {
        when(fisicoService.getById(1L)).thenReturn(fisicoResponse);

        mockMvc.perform(get("/clientes/fisicos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cpf").value("743.688.910-90"));
    }

    @Test
    void getFisicoById_ShouldReturnNotFound_WhenClientDoesNotExist() throws Exception {
        when(fisicoService.getById(999L)).thenThrow(new EntityNotFoundException("Cliente não encontrado"));

        mockMvc.perform(get("/clientes/fisicos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateFisico_ShouldReturnUpdatedClient() throws Exception {
        when(fisicoService.update(eq(1L), any(ClienteFisicoDTO.Request.class)))
                .thenReturn(fisicoResponse);

        mockMvc.perform(put("/clientes/fisicos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fisicoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateFisico_ShouldReturnNotFound_WhenClientDoesNotExist() throws Exception {
        when(fisicoService.update(eq(999L), any(ClienteFisicoDTO.Request.class)))
                .thenThrow(new EntityNotFoundException("Cliente não encontrado"));

        mockMvc.perform(put("/clientes/fisicos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fisicoRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFisico_ShouldReturnNoContent() throws Exception {
        doNothing().when(fisicoService).delete(1L);

        mockMvc.perform(delete("/clientes/fisicos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteFisico_ShouldReturnNotFound_WhenClientDoesNotExist() throws Exception {
        doThrow(new EntityNotFoundException("Cliente não encontrado")).when(fisicoService).delete(999L);

        mockMvc.perform(delete("/clientes/fisicos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllFisicos_ShouldReturnPaginatedResults() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClienteFisicoDTO.Response> page = new PageImpl<>(List.of(fisicoResponse), pageable, 1);

        when(fisicoService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/clientes/fisicos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getFisicoByCpf_ShouldReturnClient() throws Exception {
        // Service receives formatted CPF and cleans it internally
        when(fisicoService.findByCpf("743.688.910-90")).thenReturn(fisicoResponse);

        mockMvc.perform(get("/clientes/fisicos/cpf/743.688.910-90"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("743.688.910-90"));
    }

    @Test
    void getFisicoByCpf_ShouldReturnNotFound_WhenClientDoesNotExist() throws Exception {
        when(fisicoService.findByCpf("999.999.999-99")).thenThrow(new EntityNotFoundException("Cliente não encontrado"));

        mockMvc.perform(get("/clientes/fisicos/cpf/999.999.999-99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFisicoByCpf_ShouldReturnBadRequest_WhenInvalidCpf() throws Exception {
        when(fisicoService.findByCpf("invalid")).thenThrow(new IllegalArgumentException("CPF inválido"));

        mockMvc.perform(get("/clientes/fisicos/cpf/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inativarFisico_ShouldReturnNoContent() throws Exception {
        doNothing().when(fisicoService).inativarCliente(1L);

        mockMvc.perform(patch("/clientes/fisicos/1/inativar"))
                .andExpect(status().isNoContent());
    }

    @Test
    void ativarFisico_ShouldReturnNoContent() throws Exception {
        doNothing().when(fisicoService).ativarCliente(1L);

        mockMvc.perform(patch("/clientes/fisicos/1/ativar"))
                .andExpect(status().isNoContent());
    }

    @Test
    void inativarPorTipo_Fisica_ShouldReturnNoContent() throws Exception {
        doNothing().when(fisicoService).inativarCliente(1L);

        mockMvc.perform(patch("/clientes/1/inativar")
                        .param("tipo", "FISICA"))
                .andExpect(status().isNoContent());
    }

    @Test
    void inativarPorTipo_ShouldReturnBadRequest_WhenInvalidTipo() throws Exception {
        mockMvc.perform(patch("/clientes/1/inativar")
                        .param("tipo", "INVALIDO"))
                .andExpect(status().isBadRequest());
    }
}