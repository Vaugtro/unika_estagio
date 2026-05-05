package com.desafio.estagio.mvc.controller;

import com.desafio.estagio.mvc.model.dto.ClienteFisicoDTO;
import com.desafio.estagio.mvc.model.dto.ClienteJuridicoDTO;
import com.desafio.estagio.mvc.model.dto.TipoCliente;
import com.desafio.estagio.mvc.model.entity.ClienteFisicoEntity;
import com.desafio.estagio.mvc.model.entity.ClienteJuridicoEntity;
import com.desafio.estagio.mvc.service.ClienteFisicoService;
import com.desafio.estagio.mvc.service.ClienteJuridicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes (Físicos e Jurídicos)")
public class ClienteController {

    private final ClienteFisicoService fisicoService;
    private final ClienteJuridicoService juridicoService;

    // =====================================================
    // CLIENTE FÍSICO ENDPOINTS
    // =====================================================

    @Operation(summary = "Listar todos os clientes físicos", description = "Retorna uma lista com todos os clientes pessoa física cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/fisicos")
    public ResponseEntity<List<ClienteFisicoDTO.Response>> getAllFisicos() {
        return ResponseEntity.ok(fisicoService.findAll());
    }

    @Operation(summary = "Buscar cliente físico por ID", description = "Retorna um cliente físico específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/fisicos/{id}")
    public ResponseEntity<ClienteFisicoEntity> getFisicoById(@PathVariable Long id) {
        return ResponseEntity.ok(fisicoService.findById(id));
    }

    @Operation(summary = "Criar um novo cliente físico", description = "Cadastra um novo cliente pessoa física no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "CPF ou RG já cadastrado")
    })
    @PostMapping("/fisicos")
    public ResponseEntity<ClienteFisicoDTO.Response> createFisico(
            @RequestBody @Valid ClienteFisicoDTO.Request request) {
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
            @RequestBody @Valid ClienteFisicoDTO.Request request) {
        return ResponseEntity.ok(fisicoService.update(id, request));
    }

    @Operation(summary = "Inativar cliente físico", description = "Inativa um cliente físico (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PatchMapping("/fisicos/{id}/inativar")
    public ResponseEntity<Void> inativarFisico(@PathVariable Long id) {
        fisicoService.inativarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ativar cliente físico", description = "Reativa um cliente físico que estava inativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PatchMapping("/fisicos/{id}/ativar")
    public ResponseEntity<Void> ativarFisico(@PathVariable Long id) {
        fisicoService.ativarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletar cliente físico", description = "Remove permanentemente um cliente físico do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/fisicos/{id}")
    public ResponseEntity<Void> deleteFisico(@PathVariable Long id) {
        fisicoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // CLIENTE JURÍDICO ENDPOINTS
    // =====================================================

    @Operation(summary = "Listar todos os clientes jurídicos", description = "Retorna uma lista com todos os clientes pessoa jurídica cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/juridicos")
    public ResponseEntity<List<ClienteJuridicoDTO.Response>> getAllJuridicos() {
        return ResponseEntity.ok(juridicoService.findAll());
    }

    @Operation(summary = "Buscar cliente jurídico por ID", description = "Retorna um cliente jurídico específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/juridicos/{id}")
    public ResponseEntity<ClienteJuridicoEntity> getJuridicoById(@PathVariable Long id) {
        return ResponseEntity.ok(juridicoService.findById(id));
    }

    @Operation(summary = "Criar um novo cliente jurídico", description = "Cadastra um novo cliente pessoa jurídica no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "CNPJ já cadastrado")
    })
    @PostMapping("/juridicos")
    public ResponseEntity<ClienteJuridicoDTO.Response> createJuridico(
            @RequestBody @Valid ClienteJuridicoDTO.Request request) {
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
            @RequestBody @Valid ClienteJuridicoDTO.Request request) {
        return ResponseEntity.ok(juridicoService.update(id, request));
    }

    @Operation(summary = "Inativar cliente jurídico", description = "Inativa um cliente jurídico (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PatchMapping("/juridicos/{id}/inativar")
    public ResponseEntity<Void> inativarJuridico(@PathVariable Long id) {
        juridicoService.inativarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ativar cliente jurídico", description = "Reativa um cliente jurídico que estava inativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PatchMapping("/juridicos/{id}/ativar")
    public ResponseEntity<Void> ativarJuridico(@PathVariable Long id) {
        juridicoService.ativarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletar cliente jurídico", description = "Remove permanentemente um cliente jurídico do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/juridicos/{id}")
    public ResponseEntity<Void> deleteJuridico(@PathVariable Long id) {
        juridicoService.delete(id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<Void> inativarPorTipo(
            @Parameter(description = "ID do cliente", required = true) @PathVariable Long id,
            @Parameter(description = "Tipo do cliente (FISICA ou JURIDICA)", required = true) @RequestParam TipoCliente tipo) {

        if (tipo == TipoCliente.FISICA) {
            fisicoService.inativarCliente(id);
        } else if (tipo == TipoCliente.JURIDICA) {
            juridicoService.inativarCliente(id);
        } else {
            return ResponseEntity.badRequest().build();
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
    public ResponseEntity<Void> ativarPorTipo(
            @PathVariable Long id,
            @RequestParam TipoCliente tipo) {

        if (tipo == TipoCliente.FISICA) {
            fisicoService.ativarCliente(id);
        } else if (tipo == TipoCliente.JURIDICA) {
            juridicoService.ativarCliente(id);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // BUSCAS POR CPF/CNPJ
    // =====================================================

    @Operation(summary = "Buscar cliente físico por CPF", description = "Retorna um cliente físico pelo número do CPF")
    @GetMapping("/fisicos/cpf/{cpf}")
    public ResponseEntity<ClienteFisicoDTO.Response> getFisicoByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(fisicoService.findByCpf(cpf));
    }

    @Operation(summary = "Buscar cliente jurídico por CNPJ", description = "Retorna um cliente jurídico pelo número do CNPJ")
    @GetMapping("/juridicos/cnpj/{cnpj}")
    public ResponseEntity<ClienteJuridicoDTO.Response> getJuridicoByCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(juridicoService.findByCnpj(cnpj));
    }
}