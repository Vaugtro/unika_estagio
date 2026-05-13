package com.desafio.estagio.controller;

import com.desafio.estagio.dto.clientejuridico.*;
import com.desafio.estagio.service.ClienteJuridicoService;
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
@RequestMapping("/v1/clientes/juridicos")
@RequiredArgsConstructor
@Tag(name = "Clientes Jurídicos", description = "Endpoints para gerenciamento de clientes pessoa jurídica")
public class ClienteJuridicoController {

    private final ClienteJuridicoService juridicoService;

    @Operation(summary = "Listar todos os clientes jurídicos", description = "Retorna uma página com os clientes pessoa jurídica cadastrados")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    @GetMapping
    public ResponseEntity<Page<ClienteJuridicoListResponse>> getAll(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(juridicoService.findAll(pageable));
    }

    @Operation(summary = "Listar clientes jurídicos ativos", description = "Retorna uma página com os clientes pessoa jurídica ativos")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    @GetMapping("/ativos")
    public ResponseEntity<Page<ClienteJuridicoListResponse>> getAllActive(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(juridicoService.findAllActive(pageable));
    }

    @Operation(summary = "Buscar cliente jurídico por ID", description = "Retorna um cliente jurídico específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteJuridicoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(juridicoService.findById(id));
    }

    @Operation(summary = "Buscar cliente jurídico por CNPJ", description = "Retorna um cliente jurídico pelo número do CNPJ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<ClienteJuridicoResponse> getByCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(juridicoService.findByCnpj(cnpj));
    }

    @Operation(summary = "Verificar se CNPJ já existe", description = "Verifica se um CNPJ já está cadastrado no sistema")
    @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    @GetMapping("/cnpj/{cnpj}/exists")
    public ResponseEntity<Boolean> existsByCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(juridicoService.existsByCnpj(cnpj));
    }

    @Operation(summary = "Criar um novo cliente jurídico", description = "Cadastra um novo cliente pessoa jurídica no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente sem endereço principal"),
            @ApiResponse(responseCode = "409", description = "CNPJ já cadastrado")
    })
    @PostMapping
    public ResponseEntity<ClienteJuridicoResponse> create(
            @RequestBody @Valid ClienteJuridicoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(juridicoService.create(request));
    }

    @Operation(summary = "Atualizar cliente jurídico", description = "Atualiza os dados de um cliente jurídico existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteJuridicoResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid ClienteJuridicoUpdateRequest request) {
        return ResponseEntity.ok(juridicoService.update(id, request));
    }

    @Operation(summary = "Ativar cliente jurídico", description = "Reativa um cliente jurídico que estava inativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cliente já está ativo")
    })
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        juridicoService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Inativar cliente jurídico", description = "Inativa um cliente jurídico (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cliente já está inativo")
    })
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inactivate(@PathVariable Long id) {
        juridicoService.inactivate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletar cliente jurídico", description = "Remove permanentemente um cliente jurídico do sistema (hard delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cliente possui endereços associados")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> hardDelete(@PathVariable Long id) {
        juridicoService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Gerar relatório de clientes jurídicos", description = "Retorna dados para geração de relatório de clientes jurídicos")
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    @GetMapping("/relatorio")
    public ResponseEntity<Page<ClienteJuridicoReportResponse>> getReport(
            @Parameter(description = "Parâmetros de paginação (page, size)")
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(juridicoService.findAllForReport(pageable));
    }
}