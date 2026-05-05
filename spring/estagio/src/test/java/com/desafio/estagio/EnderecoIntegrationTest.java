package com.desafio.estagio;

import com.desafio.estagio.mvc.model.dto.EnderecoDTO;
import com.desafio.estagio.mvc.model.entity.ClienteFisicoEntity;
import com.desafio.estagio.mvc.model.entity.EnderecoEntity;
import com.desafio.estagio.repository.ClienteRepository;
import com.desafio.estagio.repository.EnderecoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@Transactional
class EnderecoIntegrationTest {

    @Container
    static MariaDBContainer<?> mariadb = new MariaDBContainer<>("mariadb:11.4")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);  // Reuse container between test runs for speed

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.mariadb.jdbc.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MariaDBDialect");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository<ClienteFisicoEntity> clienteRepository;

    @Autowired
    private EnderecoRepository<EnderecoEntity> enderecoRepository;

    private Long clienteId;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        enderecoRepository.deleteAll();
        clienteRepository.deleteAll();

        // Create cliente
        ClienteFisicoEntity cliente = new ClienteFisicoEntity();
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");
        cliente.setCpf("12345678900");
        cliente.setEstaAtivo(true);

        ClienteFisicoEntity saved = clienteRepository.save(cliente);
        clienteId = saved.getId();
    }

    @Test
    void createForCliente_ShouldPersistAddress() throws Exception {
        EnderecoDTO.Request request = new EnderecoDTO.Request(
                "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", true, "Apto 42", null
        );

        mockMvc.perform(post("/api/enderecos/cliente/{clienteId}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.logradouro").value("Rua das Flores"))
                .andExpect(jsonPath("$.numero").value(123))
                .andExpect(jsonPath("$.principal").value(true));
    }

    @Test
    void findById_ShouldReturnAddress() throws Exception {
        // First create address
        EnderecoDTO.Request request = new EnderecoDTO.Request(
                "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", true, "Apto 42", null
        );

        String response = mockMvc.perform(post("/api/enderecos/cliente/{clienteId}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long addressId = objectMapper.readTree(response).get("id").asLong();

        // Then find by id
        mockMvc.perform(get("/api/enderecos/{id}", addressId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressId))
                .andExpect(jsonPath("$.logradouro").value("Rua das Flores"))
                .andExpect(jsonPath("$.principal").value(true));
    }

    @Test
    void findAllByClienteId_ShouldReturnAddressList() throws Exception {
        // Create first address
        EnderecoDTO.Request request1 = new EnderecoDTO.Request(
                "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", true, "Apto 42", null
        );

        mockMvc.perform(post("/api/enderecos/cliente/{clienteId}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Create second address
        EnderecoDTO.Request request2 = new EnderecoDTO.Request(
                "Rua Nova", 456L, "87654321", "Jardins",
                "11987654321", "Rio de Janeiro", "RJ", false, "Sala 10", null
        );

        mockMvc.perform(post("/api/enderecos/cliente/{clienteId}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        // Get all addresses for client
        mockMvc.perform(get("/api/enderecos/cliente/{clienteId}", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].logradouro").value("Rua das Flores"))
                .andExpect(jsonPath("$[1].logradouro").value("Rua Nova"));
    }

    @Test
    void updateAddress_ShouldModifyFields() throws Exception {
        // First create address
        EnderecoDTO.Request request = new EnderecoDTO.Request(
                "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", true, "Apto 42", null
        );

        String response = mockMvc.perform(post("/api/enderecos/cliente/{clienteId}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long addressId = objectMapper.readTree(response).get("id").asLong();

        // Update address
        EnderecoDTO.Request updateRequest = new EnderecoDTO.Request(
                "Rua Atualizada", 999L, "99999999", "Novo Bairro",
                "11999999999", "Rio de Janeiro", "RJ", false, "Sala 999", null
        );

        mockMvc.perform(put("/api/enderecos/{id}", addressId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Rua Atualizada"))
                .andExpect(jsonPath("$.numero").value(999))
                .andExpect(jsonPath("$.cidade").value("Rio de Janeiro"))
                .andExpect(jsonPath("$.principal").value(false));
    }

    @Test
    void setAsPrincipal_ShouldUpdatePrincipalAddress() throws Exception {
        // Create first address (principal)
        EnderecoDTO.Request request1 = new EnderecoDTO.Request(
                "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", true, "Apto 42", null
        );

        mockMvc.perform(post("/api/enderecos/cliente/{clienteId}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Create second address (non-principal)
        EnderecoDTO.Request request2 = new EnderecoDTO.Request(
                "Rua Nova", 456L, "87654321", "Jardins",
                "11987654321", "São Paulo", "SP", false, "Sala 10", null
        );

        String secondResponse = mockMvc.perform(post("/api/enderecos/cliente/{clienteId}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long secondAddressId = objectMapper.readTree(secondResponse).get("id").asLong();

        // Set second address as principal
        mockMvc.perform(patch("/api/enderecos/{id}/principal", secondAddressId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.principal").value(true));
    }

    @Test
    void deleteAddress_ShouldRemoveAddress() throws Exception {
        // Create address
        EnderecoDTO.Request request = new EnderecoDTO.Request(
                "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", true, "Apto 42", null
        );

        String response = mockMvc.perform(post("/api/enderecos/cliente/{clienteId}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long addressId = objectMapper.readTree(response).get("id").asLong();

        // Delete address
        mockMvc.perform(delete("/api/enderecos/{id}", addressId))
                .andExpect(status().isNoContent());

        // Verify address is deleted
        mockMvc.perform(get("/api/enderecos/{id}", addressId))
                .andExpect(status().isNotFound());
    }

    @Test
    void findPrincipalEndereco_ShouldReturnPrincipalAddress() throws Exception {
        // Create first address (principal)
        EnderecoDTO.Request request1 = new EnderecoDTO.Request(
                "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", true, "Apto 42", null
        );

        mockMvc.perform(post("/api/enderecos/cliente/{clienteId}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Create second address (non-principal)
        EnderecoDTO.Request request2 = new EnderecoDTO.Request(
                "Rua Nova", 456L, "87654321", "Jardins",
                "11987654321", "São Paulo", "SP", false, "Sala 10", null
        );

        mockMvc.perform(post("/api/enderecos/cliente/{clienteId}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        // Get principal address
        mockMvc.perform(get("/api/enderecos/cliente/{clienteId}/principal", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Rua das Flores"))
                .andExpect(jsonPath("$.principal").value(true));
    }
}