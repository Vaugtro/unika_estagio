package com.desafio.estagio.controller;

import com.desafio.estagio.dto.endereco.*;
import com.desafio.estagio.service.EnderecoService;
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
@RequestMapping("/v1/enderecos")
@RequiredArgsConstructor
@Tag(name = "Endereços", description = "Endpoints para gerenciamento de endereços dos clientes (Físicos e Jurídicos)")
public class EnderecoController {

    private final EnderecoService enderecoService;

    // =====================================================
    // CREATE OPERATIONS
    // =====================================================

    @Operation(summary = "Criar um novo endereço", description = "Cadastra um novo endereço no sistema (cliente deve ser informado no request)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente não informado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PostMapping
    public ResponseEntity<EnderecoResponse> create(@Valid @RequestBody EnderecoCreateRequest request) {
        EnderecoResponse response = enderecoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Criar endereço para um cliente específico", description = "Cadastra um novo endereço associado a um cliente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PostMapping("/clientes/{clienteId}")
    public ResponseEntity<EnderecoResponse> createForCliente(
            @Parameter(description = "ID do cliente", required = true) @PathVariable Long clienteId,
            @Valid @RequestBody EnderecoWithinClienteCreateRequest request) {
        EnderecoResponse response = enderecoService.createForCliente(clienteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =====================================================
    // READ OPERATIONS
    // =====================================================

    @Operation(summary = "Buscar endereço por ID", description = "Retorna um endereço específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(enderecoService.findById(id));
    }

    @Operation(summary = "Listar todos os endereços de um cliente", description = "Retorna todos os endereços associados a um cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/clientes/{clienteId}")
    public ResponseEntity<Page<EnderecoListResponse>> findAllByClienteId(
            @Parameter(description = "ID do cliente", required = true) @PathVariable Long clienteId,
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(enderecoService.findAllByClienteId(clienteId, pageable));
    }

    @Operation(summary = "Buscar endereço principal de um cliente", description = "Retorna o endereço marcado como principal para um cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço principal encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente ou endereço principal não encontrado")
    })
    @GetMapping("/clientes/{clienteId}/principal")
    public ResponseEntity<EnderecoResponse> findPrincipalByClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(enderecoService.findPrincipalByClienteId(clienteId));
    }

    @Operation(summary = "Contar endereços de um cliente", description = "Retorna a quantidade de endereços associados a um cliente")
    @ApiResponse(responseCode = "200", description = "Contagem realizada com sucesso")
    @GetMapping("/clientes/{clienteId}/count")
    public ResponseEntity<Long> countByClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(enderecoService.countByClienteId(clienteId));
    }

    // =====================================================
    // UPDATE OPERATIONS
    // =====================================================

    @Operation(summary = "Atualizar endereço", description = "Atualiza os dados de um endereço existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EnderecoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EnderecoUpdateRequest request) {
        return ResponseEntity.ok(enderecoService.update(id, request));
    }

    @Operation(summary = "Definir endereço como principal", description = "Marca um endereço como principal para o cliente (remove principal anterior)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço definido como principal com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
            @ApiResponse(responseCode = "400", description = "Endereço já é principal ou cliente sem endereços")
    })
    @PatchMapping("/{id}/principal")
    public ResponseEntity<EnderecoResponse> setAsPrincipal(@PathVariable Long id) {
        return ResponseEntity.ok(enderecoService.setAsPrincipal(id));
    }

    // =====================================================
    // DELETE OPERATIONS
    // =====================================================

    @Operation(summary = "Deletar endereço", description = "Remove um endereço do sistema (não permite deletar o último endereço do cliente)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
            @ApiResponse(responseCode = "400", description = "Não é possível deletar o único endereço do cliente")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        enderecoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletar todos os endereços de um cliente", description = "Remove todos os endereços associados a um cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereços deletados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/clientes/{clienteId}")
    public ResponseEntity<Void> deleteAllByClienteId(@PathVariable Long clienteId) {
        enderecoService.deleteAllByClienteId(clienteId);
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // VALIDATION OPERATIONS
    // =====================================================

    @Operation(summary = "Verificar se cliente possui endereços", description = "Verifica se um cliente tem pelo menos um endereço cadastrado")
    @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    @GetMapping("/clientes/{clienteId}/has-addresses")
    public ResponseEntity<Boolean> hasAtLeastOneAddress(@PathVariable Long clienteId) {
        return ResponseEntity.ok(enderecoService.hasAtLeastOneAddress(clienteId));
    }

    @Operation(summary = "Verificar se cliente possui endereço principal", description = "Verifica se um cliente tem um endereço marcado como principal")
    @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    @GetMapping("/clientes/{clienteId}/has-principal")
    public ResponseEntity<Boolean> hasPrincipalAddress(@PathVariable Long clienteId) {
        return ResponseEntity.ok(enderecoService.hasPrincipalAddress(clienteId));
    }
}