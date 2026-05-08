package com.desafio.estagio;

import com.desafio.estagio.controller.ClienteController;
import com.desafio.estagio.dto.ClienteJuridicoDTO;
import com.desafio.estagio.dto.EnderecoDTO;
import com.desafio.estagio.exceptions.handlers.GlobalExceptionHandler;
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
class ClienteJuridicoControllerTest {

    @Mock
    private ClienteFisicoService fisicoService;

    @Mock
    private ClienteJuridicoService juridicoService;

    @InjectMocks
    private ClienteController clienteController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ClienteJuridicoDTO.Request juridicoRequest;
    private ClienteJuridicoDTO.Response juridicoResponse;
    private EnderecoDTO.Request enderecoRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(clienteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        enderecoRequest = new EnderecoDTO.Request(
                "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", true, "Apto 42", null
        );

        // Request with only the fields that exist
        juridicoRequest = new ClienteJuridicoDTO.Request(
                TipoCliente.JURIDICA,
                "contato@empresa.com",
                "84.758.766/0001-20",
                "Empresa LTDA",
                "Empresa LTDA",
                true,
                LocalDate.of(1998, 7, 14),
                List.of(enderecoRequest)
        );

        // Response matches what the service returns
        juridicoResponse = new ClienteJuridicoDTO.Response(
                1L,
                TipoCliente.JURIDICA,
                "contato@empresa.com",
                "84.758.766/0001-20",
                "Empresa LTDA",
                "Empresa LTDA",
                true,
                LocalDate.of(1998, 7, 14),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    // ==================== CREATE TESTS ====================

    @Test
    void createJuridico_ShouldReturnCreatedClient() throws Exception {
        when(juridicoService.create(any(ClienteJuridicoDTO.Request.class)))
                .thenReturn(juridicoResponse);

        mockMvc.perform(post("/clientes/juridicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(juridicoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.razaoSocial").value("Empresa LTDA"))
                .andExpect(jsonPath("$.cnpj").value("84.758.766/0001-20"))
                .andExpect(jsonPath("$.tipo").value("JURIDICA"))
                .andExpect(jsonPath("$.email").value("contato@empresa.com"));
    }

    @Test
    void createJuridico_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        ClienteJuridicoDTO.Request invalidRequest = new ClienteJuridicoDTO.Request(
                null, "", "", "", "", null, null, null
        );

        mockMvc.perform(post("/clientes/juridicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createJuridico_ShouldReturnConflict_WhenCnpjAlreadyExists() throws Exception {
        when(juridicoService.create(any(ClienteJuridicoDTO.Request.class)))
                .thenThrow(new RuntimeException("CNPJ já cadastrado"));

        mockMvc.perform(post("/clientes/juridicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(juridicoRequest)))
                .andExpect(status().isConflict());
    }

    // ==================== GET BY ID TESTS ====================

    @Test
    void getJuridicoById_ShouldReturnClient() throws Exception {
        when(juridicoService.getById(1L)).thenReturn(juridicoResponse);

        mockMvc.perform(get("/clientes/juridicos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.razaoSocial").value("Empresa LTDA"))
                .andExpect(jsonPath("$.cnpj").value("84.758.766/0001-20"));
    }

    @Test
    void getJuridicoById_ShouldReturnNotFound_WhenClientDoesNotExist() throws Exception {
        when(juridicoService.getById(999L)).thenThrow(new EntityNotFoundException("Cliente não encontrado"));

        mockMvc.perform(get("/clientes/juridicos/999"))
                .andExpect(status().isNotFound());
    }

    // ==================== GET ALL TESTS ====================

    @Test
    void getAllJuridicos_ShouldReturnPaginatedResults() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClienteJuridicoDTO.Response> page = new PageImpl<>(List.of(juridicoResponse), pageable, 1);

        when(juridicoService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/clientes/juridicos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].razaoSocial").value("Empresa LTDA"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getAllJuridicos_ShouldReturnEmptyPage_WhenNoClients() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClienteJuridicoDTO.Response> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(juridicoService.findAll(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/clientes/juridicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ==================== UPDATE TESTS ====================

    @Test
    void updateJuridico_ShouldReturnUpdatedClient() throws Exception {
        when(juridicoService.update(eq(1L), any(ClienteJuridicoDTO.Request.class)))
                .thenReturn(juridicoResponse);

        mockMvc.perform(put("/clientes/juridicos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(juridicoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.razaoSocial").value("Empresa LTDA"));
    }

    @Test
    void updateJuridico_ShouldReturnNotFound_WhenClientDoesNotExist() throws Exception {
        when(juridicoService.update(eq(999L), any(ClienteJuridicoDTO.Request.class)))
                .thenThrow(new EntityNotFoundException("Cliente não encontrado"));

        mockMvc.perform(put("/clientes/juridicos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(juridicoRequest)))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE TESTS ====================

    @Test
    void deleteJuridico_ShouldReturnNoContent() throws Exception {
        doNothing().when(juridicoService).delete(1L);

        mockMvc.perform(delete("/clientes/juridicos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteJuridico_ShouldReturnNotFound_WhenClientDoesNotExist() throws Exception {
        doThrow(new EntityNotFoundException("Cliente não encontrado")).when(juridicoService).delete(999L);

        mockMvc.perform(delete("/clientes/juridicos/999"))
                .andExpect(status().isNotFound());
    }

    // ==================== ACTIVATION/INACTIVATION TESTS ====================

    @Test
    void inativarJuridico_ShouldReturnNoContent() throws Exception {
        doNothing().when(juridicoService).inativarCliente(1L);

        mockMvc.perform(patch("/clientes/juridicos/1/inativar"))
                .andExpect(status().isNoContent());
    }

    @Test
    void inativarJuridico_ShouldReturnNotFound_WhenClientDoesNotExist() throws Exception {
        doThrow(new EntityNotFoundException("Cliente não encontrado")).when(juridicoService).inativarCliente(999L);

        mockMvc.perform(patch("/clientes/juridicos/999/inativar"))
                .andExpect(status().isNotFound());
    }

    @Test
    void ativarJuridico_ShouldReturnNoContent() throws Exception {
        doNothing().when(juridicoService).ativarCliente(1L);

        mockMvc.perform(patch("/clientes/juridicos/1/ativar"))
                .andExpect(status().isNoContent());
    }

    @Test
    void ativarJuridico_ShouldReturnNotFound_WhenClientDoesNotExist() throws Exception {
        doThrow(new EntityNotFoundException("Cliente não encontrado")).when(juridicoService).ativarCliente(999L);

        mockMvc.perform(patch("/clientes/juridicos/999/ativar"))
                .andExpect(status().isNotFound());
    }

    // ==================== CNPJ SEARCH TESTS ====================

    @Test
    void getJuridicoByCnpj_ShouldReturnClient() throws Exception {
        // Service expects whatever format - it will clean it anyway
        when(juridicoService.findByCnpj("84758766000120")).thenReturn(juridicoResponse);  // ✅ Unformatted

        // URL must contain only digits (no special characters)
        mockMvc.perform(get("/clientes/juridicos/cnpj/84758766000120"))  // ✅ Only numbers
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cnpj").value("84.758.766/0001-20"));
    }

    @Test
    void getJuridicoByCnpj_ShouldReturnNotFound_WhenClientDoesNotExist() throws Exception {
        when(juridicoService.findByCnpj("99999999999999"))
                .thenThrow(new EntityNotFoundException("Cliente não encontrado"));

        mockMvc.perform(get("/clientes/juridicos/cnpj/99999999999999"))  // ✅ Only numbers
                .andExpect(status().isNotFound());
    }

    @Test
    void getJuridicoByCnpj_ShouldReturnBadRequest_WhenInvalidCnpj() throws Exception {
        when(juridicoService.findByCnpj("invalid")).thenThrow(new IllegalArgumentException("CNPJ inválido"));

        mockMvc.perform(get("/clientes/juridicos/cnpj/invalid"))
                .andExpect(status().isBadRequest());
    }

    // ==================== DYNAMIC TYPE TESTS ====================

    @Test
    void inativarPorTipo_Juridica_ShouldReturnNoContent() throws Exception {
        doNothing().when(juridicoService).inativarCliente(1L);

        mockMvc.perform(patch("/clientes/1/inativar")
                        .param("tipo", "JURIDICA"))
                .andExpect(status().isNoContent());
    }

    @Test
    void ativarPorTipo_Juridica_ShouldReturnNoContent() throws Exception {
        doNothing().when(juridicoService).ativarCliente(1L);

        mockMvc.perform(patch("/clientes/1/ativar")
                        .param("tipo", "JURIDICA"))
                .andExpect(status().isNoContent());
    }

    @Test
    void inativarPorTipo_ShouldReturnBadRequest_WhenInvalidTipo() throws Exception {
        mockMvc.perform(patch("/clientes/1/inativar")
                        .param("tipo", "INVALIDO"))
                .andExpect(status().isBadRequest());
    }

    // ==================== VERIFICATION TESTS ====================

    @Test
    void createJuridico_ShouldCallServiceWithCorrectData() throws Exception {
        when(juridicoService.create(any(ClienteJuridicoDTO.Request.class)))
                .thenReturn(juridicoResponse);

        mockMvc.perform(post("/clientes/juridicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(juridicoRequest)))
                .andExpect(status().isCreated());

        verify(juridicoService, times(1)).create(any(ClienteJuridicoDTO.Request.class));
    }
}