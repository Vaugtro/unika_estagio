package com.desafio.estagio;

import com.desafio.estagio.mvc.model.dto.ClienteFisicoDTO;
import com.desafio.estagio.mvc.model.dto.EnderecoDTO;
import com.desafio.estagio.mvc.model.dto.TipoCliente;
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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@Transactional
class ClienteFisicoIntegrationTest {

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

    private ClienteFisicoDTO.Request request;

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
        EnderecoDTO.Request enderecoRequest = new EnderecoDTO.Request(
                "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", true, "Apto 42", null
        );

        request = new ClienteFisicoDTO.Request(
                TipoCliente.FISICA,
                "joao@email.com",
                "318.205.730-87",
                "João Silva",
                "123456789",
                true,
                LocalDate.of(1990, 1, 15),
                List.of(enderecoRequest)
        );
    }

    @Test
    void createCliente_ShouldPersistToDatabase() throws Exception {
        mockMvc.perform(post("/clientes/fisicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void createCliente_ShouldReturnConflict_WhenDuplicateCpf() throws Exception {
        // First creation
        mockMvc.perform(post("/clientes/fisicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second with same CPF
        mockMvc.perform(post("/clientes/fisicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void getClienteById_ShouldReturnClient() throws Exception {
        // Create first
        String response = mockMvc.perform(post("/clientes/fisicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        // Get by ID
        mockMvc.perform(get("/clientes/fisicos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void getClienteByCpf_ShouldReturnClient() throws Exception {
        // Create first
        mockMvc.perform(post("/clientes/fisicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Get by CPF (unformatted in URL)
        mockMvc.perform(get("/clientes/fisicos/cpf/318.205.730-87"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("31820573087"));
    }

    @Test
    void getAllClientes_ShouldReturnPaginatedResults() throws Exception {
        mockMvc.perform(get("/clientes/fisicos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.totalElements").exists());
    }
}