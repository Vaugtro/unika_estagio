package com.desafio.estagio;

import com.desafio.estagio.mvc.model.dto.EnderecoDTO;
import com.desafio.estagio.mvc.model.dto.TipoCliente;
import com.desafio.estagio.mvc.model.entity.ClienteFisicoEntity;
import com.desafio.estagio.mvc.model.entity.EnderecoEntity;
import com.desafio.estagio.repository.ClienteRepository;
import com.desafio.estagio.repository.EnderecoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
            .withReuse(true);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.mariadb.jdbc.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MariaDBDialect");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration/test");
        registry.add("spring.flyway.enabled", () -> "false");
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

    private static volatile boolean migrationsApplied = false;

    @BeforeAll
    static void runFlywayMigrations() {
        if (!migrationsApplied) {
            synchronized (EnderecoIntegrationTest.class) {
                if (!migrationsApplied) {
                    if (!mariadb.isRunning()) {
                        mariadb.start();
                    }

                    Flyway flyway = Flyway.configure()
                            .dataSource(
                                    mariadb.getJdbcUrl(),
                                    mariadb.getUsername(),
                                    mariadb.getPassword()
                            )
                            .locations("classpath:db/migration/test")
                            .baselineOnMigrate(true)
                            .validateOnMigrate(true)
                            .cleanDisabled(false)
                            .load();

                    try {
                        flyway.clean();
                        System.out.println("Test database cleaned successfully");
                    } catch (Exception e) {
                        System.out.println("Could not clean database: " + e.getMessage());
                    }

                    flyway.migrate();
                    System.out.println("Flyway migrations applied successfully to test database");

                    migrationsApplied = true;
                }
            }
        }
    }

    @BeforeEach
    void setUp() {
        // Clean up before each test
        enderecoRepository.deleteAll();
        clienteRepository.deleteAll();

        // Create cliente with all required fields
        ClienteFisicoEntity cliente = new ClienteFisicoEntity();
        cliente.setNome("João Silva");
        cliente.setEmail("joao" + System.currentTimeMillis() + "@email.com"); // Unique email
        cliente.setCpf("318.205.730-87"); // Unique CPF
        cliente.setDataNascimento(LocalDate.of(1990, 1, 15));
        cliente.setEstaAtivo(true);
        cliente.setTipo(TipoCliente.FISICA);
        cliente.setRg("123456789");
        cliente.setCreatedAt(LocalDateTime.now());
        cliente.setUpdatedAt(LocalDateTime.now());

        ClienteFisicoEntity saved = clienteRepository.save(cliente);
        clienteId = saved.getId();

        System.out.println("Test client created with ID: " + clienteId);
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
        // Create first address (will automatically become principal due to hasNoEnderecos)
        EnderecoDTO.Request request1 = new EnderecoDTO.Request(
                "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", false, "Apto 42", null
        );

        mockMvc.perform(post("/api/enderecos/cliente/{clienteId}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Create second address (must be non-principal - send principal=false)
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
                .andExpect(jsonPath("$.length()").value(2));
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
        // Create first address (non-principal)
        EnderecoDTO.Request request1 = new EnderecoDTO.Request(
                "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", false, "Apto 42", null
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
                "11912345678", "São Paulo", "SP", false, "Apto 42", null
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

        // Verify address is deleted - should return 404
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

        // Get principal address
        mockMvc.perform(get("/api/enderecos/cliente/{clienteId}/principal", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Rua das Flores"))
                .andExpect(jsonPath("$.principal").value(true));
    }
}