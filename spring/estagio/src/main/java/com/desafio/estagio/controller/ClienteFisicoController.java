package com.desafio.estagio.controller;

import com.desafio.estagio.dto.clientefisico.*;
import com.desafio.estagio.service.ClienteFisicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/clientes/fisicos")
@RequiredArgsConstructor
@Tag(name = "Clientes Físicos", description = "Endpoints para gerenciamento de clientes pessoa física")
public class ClienteFisicoController {

    private final ClienteFisicoService fisicoService;

    @Operation(summary = "Listar todos os clientes físicos", description = "Retorna uma página com os clientes pessoa física cadastrados")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    @GetMapping
    public ResponseEntity<Page<ClienteFisicoListResponse>> getAll(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(fisicoService.findAll(pageable));
    }

    @Operation(summary = "Listar clientes físicos ativos", description = "Retorna uma página com os clientes pessoa física ativos")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    @GetMapping("/ativos")
    public ResponseEntity<Page<ClienteFisicoListResponse>> getAllActive(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(fisicoService.findAllActive(pageable));
    }

    @Operation(summary = "Buscar cliente físico por ID", description = "Retorna um cliente físico específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteFisicoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(fisicoService.findById(id));
    }

    @Operation(summary = "Buscar cliente físico por CPF", description = "Retorna um cliente físico pelo número do CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteFisicoResponse> getByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(fisicoService.findByCpf(cpf));
    }

    @Operation(summary = "Verificar se CPF já existe", description = "Verifica se um CPF já está cadastrado no sistema")
    @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    @GetMapping("/cpf/{cpf}/exists")
    public ResponseEntity<Boolean> existsByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(fisicoService.existsByCpf(cpf));
    }

    @Operation(summary = "Criar um novo cliente físico", description = "Cadastra um novo cliente pessoa física no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente sem endereço principal"),
            @ApiResponse(responseCode = "409", description = "CPF já cadastrado")
    })
    @PostMapping
    public ResponseEntity<ClienteFisicoResponse> create(
            @RequestBody @Valid ClienteFisicoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fisicoService.create(request));
    }

    @Operation(summary = "Atualizar cliente físico", description = "Atualiza os dados de um cliente físico existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteFisicoResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid ClienteFisicoUpdateRequest request) {
        return ResponseEntity.ok(fisicoService.update(id, request));
    }

    @Operation(summary = "Ativar cliente físico", description = "Reativa um cliente físico que estava inativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cliente já está ativo")
    })
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        fisicoService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Inativar cliente físico", description = "Inativa um cliente físico (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cliente já está inativo")
    })
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inactivate(@PathVariable Long id) {
        fisicoService.inactivate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Soft delete cliente físico", description = "Inativa um cliente físico (mesmo que inativar)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        fisicoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Hard delete cliente físico", description = "Remove permanentemente um cliente físico do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cliente possui endereços associados")
    })
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> hardDelete(@PathVariable Long id) {
        fisicoService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Gerar relatório de clientes físicos", description = "Retorna dados para geração de relatório de clientes físicos")
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    @GetMapping("/relatorio")
    public ResponseEntity<Page<ClienteFisicoReportResponse>> getReport(
            @Parameter(description = "Parâmetros de paginação (page, size)")
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(fisicoService.findAllForReport(pageable));
    }
}