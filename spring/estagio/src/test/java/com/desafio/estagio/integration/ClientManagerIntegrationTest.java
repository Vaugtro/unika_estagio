package com.desafio.estagio.integration;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoCreateRequest;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoResponse;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoUpdateRequest;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoCreateRequest;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoReportResponse;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoResponse;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoUpdateRequest;
import com.desafio.estagio.dto.endereco.EnderecoResponse;
import com.desafio.estagio.dto.endereco.EnderecoWithinClienteCreateRequest;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.service.EnderecoService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("End-to-End Integration Tests")
class ClientManagerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ClienteFisicoService fisicoService;

    @Autowired
    private ClienteJuridicoService juridicoService;

    @Autowired
    private EnderecoService enderecoService;

    private Long fisicoId;
    private Long juridicoId;

    // ============================================================
    // CLIENTE FISICO — Full CRUD + lifecycle
    // ============================================================

    @Test
    @DisplayName("Fisico: create -> find -> update -> inactivate -> activate")
    @Order(1)
    void testFisicoFullCycle() {
        // CREATE
        var endereco = new EnderecoWithinClienteCreateRequest(
                "Rua das Flores", 123L, "01001-000", "Centro",
                "(11) 91234-5678", "SP", "São Paulo", true, null
        );
        var createReq = new ClienteFisicoCreateRequest(
                "123.456.789-01", "João Silva", "123456789",
                "joao@example.com", LocalDate.of(1990, 5, 15), java.util.List.of(endereco)
        );

        ClienteFisicoResponse created = fisicoService.create(createReq);
        assertThat(created).isNotNull();
        assertThat(created.id()).isPositive();
        assertThat(created.nome()).isEqualTo("João Silva");
        assertThat(created.cpf()).isEqualTo("123.456.789-01");
        assertThat(created.estaAtivo()).isTrue();
        fisicoId = created.id();

        // FIND BY ID
        ClienteFisicoResponse found = fisicoService.findById(fisicoId);
        assertThat(found.nome()).isEqualTo("João Silva");

        // UPDATE
        var updateReq = new ClienteFisicoUpdateRequest("João Silva Updated", "joao.novo@example.com", true);
        ClienteFisicoResponse updated = fisicoService.update(fisicoId, updateReq);
        assertThat(updated.nome()).isEqualTo("João Silva Updated");

        // FIND AFTER UPDATE
        found = fisicoService.findById(fisicoId);
        assertThat(found.nome()).isEqualTo("João Silva Updated");

        // INACTIVATE
        fisicoService.inactivate(fisicoId);
        found = fisicoService.findById(fisicoId);
        assertThat(found.estaAtivo()).isFalse();

        // ACTIVATE
        fisicoService.activate(fisicoId);
        found = fisicoService.findById(fisicoId);
        assertThat(found.estaAtivo()).isTrue();
    }

    // ============================================================
    // CLIENTE JURIDICO — Full CRUD + lifecycle
    // ============================================================

    @Test
    @DisplayName("Juridico: create -> find -> update -> inactivate -> activate")
    @Order(2)
    void testJuridicoFullCycle() {
        var endereco = new com.desafio.estagio.dto.endereco.EnderecoCreateRequest(
                "Av Paulista", 1000L, "01310-100", "Bela Vista",
                "(11) 99999-8888", "SP", "São Paulo", true, "Sala 1", null
        );
        var createReq = new ClienteJuridicoCreateRequest(
                "11.222.333/0001-81", "Empresa Exemplo LTDA", "123456789",
                "contato@empresa.com", LocalDate.of(2020, 1, 15), java.util.List.of(endereco)
        );

        ClienteJuridicoResponse created = juridicoService.create(createReq);
        assertThat(created).isNotNull();
        assertThat(created.id()).isPositive();
        assertThat(created.razaoSocial()).isEqualTo("Empresa Exemplo LTDA");
        assertThat(created.cnpj()).isEqualTo("11.222.333/0001-81");
        assertThat(created.estaAtivo()).isTrue();
        juridicoId = created.id();

        // FIND BY ID
        ClienteJuridicoResponse found = juridicoService.findById(juridicoId);
        assertThat(found.razaoSocial()).isEqualTo("Empresa Exemplo LTDA");

        // UPDATE
        var updateReq = new ClienteJuridicoUpdateRequest(
                "Empresa Exemplo Atualizada", "987654321",
                "novo@empresa.com", LocalDate.of(2020, 1, 15), true, java.util.List.of());
        ClienteJuridicoResponse updated = juridicoService.update(juridicoId, updateReq);
        assertThat(updated.razaoSocial()).isEqualTo("Empresa Exemplo Atualizada");

        // FIND AFTER UPDATE
        found = juridicoService.findById(juridicoId);
        assertThat(found.razaoSocial()).isEqualTo("Empresa Exemplo Atualizada");

        // INACTIVATE
        juridicoService.inactivate(juridicoId);
        found = juridicoService.findById(juridicoId);
        assertThat(found.estaAtivo()).isFalse();

        // ACTIVATE
        juridicoService.activate(juridicoId);
        found = juridicoService.findById(juridicoId);
        assertThat(found.estaAtivo()).isTrue();
    }

    // ============================================================
    // ENDERECO — CRUD
    // ============================================================

    @Test
    @DisplayName("Endereco: create for fisico -> add second -> verify -> delete")
    @Order(3)
    void testEnderecoFullCycle() {
        // First create a fisico to own the addresses
        var createFisicoEnd = new EnderecoWithinClienteCreateRequest(
                "Rua A", 10L, "02001-000", "Vila A",
                "(11) 91111-1111", "SP", "São Paulo", true, null
        );
        var fisicoReq = new ClienteFisicoCreateRequest(
                "987.654.321-00", "Maria Souza", "987654321",
                "maria@example.com", LocalDate.of(1985, 3, 10), java.util.List.of(createFisicoEnd)
        );
        ClienteFisicoResponse fisico = fisicoService.create(fisicoReq);
        Long clienteId = fisico.id();

        // Find addresses
        var enderecos = enderecoService.findAllByClienteId(clienteId, PageRequest.of(0, 10));
        assertThat(enderecos.getContent()).hasSize(1);

        var enderecosPage = enderecoService.findAllByClienteId(clienteId, PageRequest.of(0, 10));
        Long enderecoId = enderecosPage.getContent().get(0).id();
        assertThat(enderecoId).isPositive();

        // Add a second endereco
        var endereco2 = new EnderecoWithinClienteCreateRequest(
                "Rua B", 20L, "03001-000", "Vila B",
                "(11) 92222-2222", "SP", "São Paulo", false, "Casa"
        );
        EnderecoResponse added = enderecoService.createForCliente(clienteId, endereco2);
        assertThat(added).isNotNull();
        assertThat(added.id()).isPositive();

        // Count should be 2
        long count = enderecoService.countByClienteId(clienteId);
        assertThat(count).isEqualTo(2L);

        // Delete first address
        enderecoService.delete(enderecoId);

        // Count should be 1 now
        count = enderecoService.countByClienteId(clienteId);
        assertThat(count).isEqualTo(1L);
    }

    // ============================================================
    // VALIDATION ERRORS (service level)
    // ============================================================

    @Test
    @DisplayName("Validation: non-existent ID throws exception")
    @Order(4)
    void testNotFound() {
        org.junit.jupiter.api.Assertions.assertThrows(
                com.desafio.estagio.exceptions.ResourceNotFoundException.class,
                () -> fisicoService.findById(99999L)
        );
    }

    // ============================================================
    // LISTING & REPORTING
    // ============================================================

    @Test
    @DisplayName("List: fisicos returns paginated results")
    @Order(5)
    void testListFisicos() {
        Page<ClienteFisicoListResponse> page = fisicoService.findAll(PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getTotalElements()).isPositive();
    }

    @Test
    @DisplayName("Report: juridicos report returns data")
    @Order(6)
    void testJuridicoReport() {
        Page<ClienteJuridicoReportResponse> page = juridicoService.findAllForReport(PageRequest.of(0, 50));
        assertThat(page.getContent()).isNotEmpty();
    }
}
