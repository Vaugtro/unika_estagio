package com.desafio.estagio.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for full integration tests.
 * Spins up a throwaway MariaDB container via Testcontainers with Hibernate DDL update,
 * and cleans up test data after all tests in the class run.
 * The container is destroyed when the JVM exits — no impact on the dev database.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractIntegrationTest {

    private static final MariaDBContainer<?> mariadb;

    static {
        mariadb = new MariaDBContainer<>("mariadb:11.4")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        mariadb.start();
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);
        registry.add("spring.datasource.driver-class-name", mariadb::getDriverClassName);
    }

    @AfterAll
    void cleanTestData() {
        jdbcTemplate.execute("DELETE FROM endereco");
        jdbcTemplate.execute("DELETE FROM cliente_fisico");
        jdbcTemplate.execute("DELETE FROM cliente_juridico");
        jdbcTemplate.execute("DELETE FROM cliente");
    }
}
