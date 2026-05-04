package com.desafio.estagio.mvc.controller;

import com.desafio.estagio.mvc.model.dto.ClienteFisicoDTO;
import com.desafio.estagio.mvc.model.dto.TipoCliente;
import com.desafio.estagio.mvc.service.ClienteFisicoService;
import com.desafio.estagio.mvc.service.ClienteJuridicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteFisicoService fisicoService;
    private final ClienteJuridicoService juridicoService;

    // GET /clientes/fisicos
    @GetMapping("/fisicos")
    public ResponseEntity<List<ClienteFisicoDTO.Response>> getAllFisicos() {
        return ResponseEntity.ok(fisicoService.findAll());
    }

    // POST /clientes/fisicos
    @PostMapping("/fisicos")
    public ResponseEntity<ClienteFisicoDTO.Response> createFisico(@RequestBody @Valid ClienteFisicoDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fisicoService.create(request));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id, @RequestParam TipoCliente tipo) {
        if (tipo == TipoCliente.FISICA) {
            fisicoService.inativarCliente(id);
        } else {
            juridicoService.inativarCliente(id);
        }
        return ResponseEntity.noContent().build();
    }
}