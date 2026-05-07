package com.desafio.estagio.mvc.controller;

import com.desafio.estagio.mvc.model.dto.EnderecoDTO;
import com.desafio.estagio.mvc.service.EnderecoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enderecos")
@Tag(name = "Endereços", description = "Endpoints para gerenciamento de endereços dos clientes (Físicos e Jurídicos)")
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @PostMapping
    public ResponseEntity<EnderecoDTO.Response> create(@Valid @RequestBody EnderecoDTO.Request request) {
        EnderecoDTO.Response response = enderecoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<EnderecoDTO.Response> createForCliente(
            @PathVariable Long clienteId,
            @Valid @RequestBody EnderecoDTO.Request request) {
        EnderecoDTO.Response response = enderecoService.createForCliente(clienteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnderecoDTO.Response> findById(@PathVariable Long id) {
        try {
            EnderecoDTO.Response response = enderecoService.findById(id);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<EnderecoDTO.Response>> findAllByClienteId(@PathVariable Long clienteId) {
        List<EnderecoDTO.Response> responses = enderecoService.findAllByClienteId(clienteId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<EnderecoDTO.Response>> findAll() {
        List<EnderecoDTO.Response> responses = enderecoService.findAll();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnderecoDTO.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody EnderecoDTO.Request request) {
        try {
            EnderecoDTO.Response response = enderecoService.update(id, request);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/principal")
    public ResponseEntity<EnderecoDTO.Response> setAsPrincipal(@PathVariable Long id) {
        try {
            EnderecoDTO.Response response = enderecoService.setAsPrincipal(id);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            enderecoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cliente/{clienteId}/principal")
    public ResponseEntity<EnderecoDTO.Response> findPrincipalEnderecoByClienteId(@PathVariable Long clienteId) {
        try {
            EnderecoDTO.Response response = enderecoService.findPrincipalEnderecoByClienteId(clienteId);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}