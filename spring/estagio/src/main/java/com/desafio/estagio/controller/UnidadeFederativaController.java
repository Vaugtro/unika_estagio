package com.desafio.estagio.controller;

import com.desafio.estagio.dto.unidadefederativa.UnidadeFederativaDTO;
import com.desafio.estagio.repository.UnidadeFederativaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/unidades-federativas")
@RequiredArgsConstructor
@Tag(name = "Unidades Federativas", description = "Consulta de unidades federativas")
public class UnidadeFederativaController {

    private final UnidadeFederativaRepository repository;

    @GetMapping
    @Operation(summary = "Listar todas as UFs ordenadas por nome")
    @ApiResponse(responseCode = "200", description = "Lista de UFs retornada com sucesso", content = @Content(mediaType = "application/json"))
    public ResponseEntity<List<UnidadeFederativaDTO>> findAll() {
        List<UnidadeFederativaDTO> dtos = repository.findAllByOrderByNome().stream()
                .map(uf -> new UnidadeFederativaDTO(uf.getId(), uf.getSigla(), uf.getNome()))
                .toList();
        return ResponseEntity.ok(dtos);
    }
}
