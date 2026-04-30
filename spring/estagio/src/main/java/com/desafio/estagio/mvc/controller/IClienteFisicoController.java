package com.desafio.estagio.mvc.controller;

import com.desafio.estagio.mvc.model.dto.ClienteFisicoRequest;
import com.desafio.estagio.mvc.model.dto.ClienteFisicoResponse;
import com.desafio.estagio.mvc.model.entity.ClienteFisico;
import com.desafio.estagio.mvc.model.service.ClienteFisicoService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cliente/fisico") // The base URL for this controller
public class IClienteFisicoController {

    private ClienteFisicoService service;

    // Spring automatically injects the Service here
    public void ClienteFisicoController(ClienteFisicoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ClienteFisicoResponse> create(@RequestBody @Valid ClienteFisicoRequest request) {
        ClienteFisicoResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}