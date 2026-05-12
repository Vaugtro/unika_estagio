package com.desafio.estagio.controller;

import com.desafio.estagio.dto.ClienteFisicoDTO;
import com.desafio.estagio.dto.ClienteJuridicoDTO;
import com.desafio.estagio.model.enums.TipoCliente;
import com.desafio.estagio.service.ClienteFisicoService;
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
@RequestMapping("/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes (Físicos e Jurídicos)")
public class ClienteController {

    private final ClienteFisicoService fisicoService;
    private final ClienteJuridicoService juridicoService;

    // =====================================================
    // CLIENTE FÍSICO ENDPOINTS
    // =====================================================

    @Operation(summary = "Listar todos os clientes físicos", description = "Retorna uma página com os clientes pessoa física cadastrados")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    @GetMapping("/fisicos")
    public ResponseEntity<Page<ClienteFisicoDTO.ListResponse>> getAllFisicos(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(fisicoService.findAll(pageable));
    }

    @Operation(summary = "Listar clientes físicos ativos", description = "Retorna uma página com os clientes pessoa física ativos")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    @GetMapping("/fisicos/ativos")
    public ResponseEntity<Page<ClienteFisicoDTO.ListResponse>> getAllFisicosActive(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(fisicoService.findAllActive(pageable));
    }

    @Operation(summary = "Buscar cliente físico por ID", description = "Retorna um cliente físico específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/fisicos/{id}")
    public ResponseEntity<ClienteFisicoDTO.Response> getFisicoById(@PathVariable Long id) {
        return ResponseEntity.ok(fisicoService.findById(id));
    }

    @Operation(summary = "Buscar cliente físico por CPF", description = "Retorna um cliente físico pelo número do CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/fisicos/cpf/{cpf}")
    public ResponseEntity<ClienteFisicoDTO.Response> getFisicoByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(fisicoService.findByCpf(cpf));
    }

    @Operation(summary = "Criar um novo cliente físico", description = "Cadastra um novo cliente pessoa física no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente sem endereço principal"),
            @ApiResponse(responseCode = "409", description = "CPF já cadastrado")
    })
    @PostMapping("/fisicos")
    public ResponseEntity<ClienteFisicoDTO.Response> createFisico(
            @RequestBody @Valid ClienteFisicoDTO.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fisicoService.create(request));
    }

    @Operation(summary = "Atualizar cliente físico", description = "Atualiza os dados de um cliente físico existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/fisicos/{id}")
    public ResponseEntity<ClienteFisicoDTO.Response> updateFisico(
            @PathVariable Long id,
            @RequestBody @Valid ClienteFisicoDTO.UpdateRequest request) {
        return ResponseEntity.ok(fisicoService.update(id, request));
    }

    @Operation(summary = "Ativar cliente físico", description = "Reativa um cliente físico que estava inativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cliente já está ativo")
    })
    @PatchMapping("/fisicos/{id}/ativar")
    public ResponseEntity<Void> activateFisico(@PathVariable Long id) {
        fisicoService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Inativar cliente físico", description = "Inativa um cliente físico (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cliente já está inativo")
    })
    @PatchMapping("/fisicos/{id}/inativar")
    public ResponseEntity<Void> inactivateFisico(@PathVariable Long id) {
        fisicoService.inactivate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Soft delete cliente físico", description = "Inativa um cliente físico (mesmo que inativar)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/fisicos/{id}")
    public ResponseEntity<Void> deleteFisico(@PathVariable Long id) {
        fisicoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Hard delete cliente físico", description = "Remove permanentemente um cliente físico do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cliente possui endereços associados")
    })
    @DeleteMapping("/fisicos/{id}/permanent")
    public ResponseEntity<Void> hardDeleteFisico(@PathVariable Long id) {
        fisicoService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // CLIENTE JURÍDICO ENDPOINTS
    // =====================================================

    @Operation(summary = "Listar todos os clientes jurídicos", description = "Retorna uma página com os clientes pessoa jurídica cadastrados")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    @GetMapping("/juridicos")
    public ResponseEntity<Page<ClienteJuridicoDTO.ListResponse>> getAllJuridicos(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(juridicoService.findAll(pageable));
    }

    @Operation(summary = "Listar clientes jurídicos ativos", description = "Retorna uma página com os clientes pessoa jurídica ativos")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    @GetMapping("/juridicos/ativos")
    public ResponseEntity<Page<ClienteJuridicoDTO.ListResponse>> getAllJuridicosActive(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(juridicoService.findAllActive(pageable));
    }

    @Operation(summary = "Buscar cliente jurídico por ID", description = "Retorna um cliente jurídico específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/juridicos/{id}")
    public ResponseEntity<ClienteJuridicoDTO.Response> getJuridicoById(@PathVariable Long id) {
        return ResponseEntity.ok(juridicoService.findById(id));
    }

    @Operation(summary = "Buscar cliente jurídico por CNPJ", description = "Retorna um cliente jurídico pelo número do CNPJ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/juridicos/cnpj/{cnpj}")
    public ResponseEntity<ClienteJuridicoDTO.Response> getJuridicoByCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(juridicoService.findByCnpj(cnpj));
    }

    @Operation(summary = "Criar um novo cliente jurídico", description = "Cadastra um novo cliente pessoa jurídica no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente sem endereço principal"),
            @ApiResponse(responseCode = "409", description = "CNPJ já cadastrado")
    })
    @PostMapping("/juridicos")
    public ResponseEntity<ClienteJuridicoDTO.Response> createJuridico(
            @RequestBody @Valid ClienteJuridicoDTO.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(juridicoService.create(request));
    }

    @Operation(summary = "Atualizar cliente jurídico", description = "Atualiza os dados de um cliente jurídico existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/juridicos/{id}")
    public ResponseEntity<ClienteJuridicoDTO.Response> updateJuridico(
            @PathVariable Long id,
            @RequestBody @Valid ClienteJuridicoDTO.UpdateRequest request) {
        return ResponseEntity.ok(juridicoService.update(id, request));
    }

    @Operation(summary = "Ativar cliente jurídico", description = "Reativa um cliente jurídico que estava inativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cliente já está ativo")
    })
    @PatchMapping("/juridicos/{id}/ativar")
    public ResponseEntity<Void> activateJuridico(@PathVariable Long id) {
        juridicoService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Inativar cliente jurídico", description = "Inativa um cliente jurídico (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cliente já está inativo")
    })
    @PatchMapping("/juridicos/{id}/inativar")
    public ResponseEntity<Void> inactivateJuridico(@PathVariable Long id) {
        juridicoService.inactivate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletar cliente jurídico", description = "Remove permanentemente um cliente jurídico do sistema (hard delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cliente possui endereços associados")
    })
    @DeleteMapping("/juridicos/{id}")
    public ResponseEntity<Void> deleteJuridico(@PathVariable Long id) {
        juridicoService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // ENDPOINTS PARA RELATÓRIOS
    // =====================================================

    @Operation(summary = "Gerar relatório de clientes físicos", description = "Retorna dados para geração de relatório de clientes físicos")
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    @GetMapping("/fisicos/relatorio")
    public ResponseEntity<Page<ClienteFisicoDTO.ReportResponse>> getFisicosReport(
            @Parameter(description = "Parâmetros de paginação (page, size)")
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(fisicoService.findAllForReport(pageable));
    }

    @Operation(summary = "Gerar relatório de clientes jurídicos", description = "Retorna dados para geração de relatório de clientes jurídicos")
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    @GetMapping("/juridicos/relatorio")
    public ResponseEntity<Page<ClienteJuridicoDTO.ReportResponse>> getJuridicosReport(
            @Parameter(description = "Parâmetros de paginação (page, size)")
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(juridicoService.findAllForReport(pageable));
    }

    // =====================================================
    // ENDPOINTS DE VALIDAÇÃO
    // =====================================================

    @Operation(summary = "Verificar se CPF já existe", description = "Verifica se um CPF já está cadastrado no sistema")
    @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    @GetMapping("/fisicos/cpf/{cpf}/exists")
    public ResponseEntity<Boolean> existsByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(fisicoService.existsByCpf(cpf));
    }

    @Operation(summary = "Verificar se CNPJ já existe", description = "Verifica se um CNPJ já está cadastrado no sistema")
    @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    @GetMapping("/juridicos/cnpj/{cnpj}/exists")
    public ResponseEntity<Boolean> existsByCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(juridicoService.existsByCnpj(cnpj));
    }

    // =====================================================
    // ENDPOINTS DINÂMICOS (BASEADOS NO TIPO)
    // =====================================================

    @Operation(summary = "Inativar cliente por tipo", description = "Inativa um cliente baseado no seu tipo (FÍSICA ou JURÍDICA)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Tipo de cliente inválido")
    })
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inactivateByTipo(
            @Parameter(description = "ID do cliente", required = true) @PathVariable Long id,
            @Parameter(description = "Tipo do cliente (FISICA ou JURIDICA)", required = true) @RequestParam TipoCliente tipo) {

        switch (tipo) {
            case FISICA -> fisicoService.inactivate(id);
            case JURIDICA -> juridicoService.inactivate(id);
            default -> throw new IllegalArgumentException("Tipo de cliente não suportado: " + tipo);
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ativar cliente por tipo", description = "Ativa um cliente baseado no seu tipo (FÍSICA ou JURÍDICA)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Tipo de cliente inválido")
    })
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> activateByTipo(
            @PathVariable Long id,
            @RequestParam TipoCliente tipo) {

        switch (tipo) {
            case FISICA -> fisicoService.activate(id);
            case JURIDICA -> juridicoService.activate(id);
            default -> throw new IllegalArgumentException("Tipo de cliente não suportado: " + tipo);
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Hard delete cliente por tipo", description = "Remove permanentemente um cliente baseado no seu tipo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> hardDeleteByTipo(
            @PathVariable Long id,
            @RequestParam TipoCliente tipo) {

        switch (tipo) {
            case FISICA -> fisicoService.hardDelete(id);
            case JURIDICA -> juridicoService.hardDelete(id);
            default -> throw new IllegalArgumentException("Tipo de cliente não suportado: " + tipo);
        }
        return ResponseEntity.noContent().build();
    }
}