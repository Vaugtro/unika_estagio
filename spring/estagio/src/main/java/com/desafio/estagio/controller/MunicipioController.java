package com.desafio.estagio.controller;

import com.desafio.estagio.dto.municipio.MunicipioDTO;
import com.desafio.estagio.repository.MunicipioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/municipios")
@RequiredArgsConstructor
@Tag(name = "Municipios", description = "Consulta de municípios")
public class MunicipioController {

    private final MunicipioRepository repository;

    @GetMapping
    @Operation(summary = "Listar municípios, filtrados por UF (opcional)")
    public ResponseEntity<List<MunicipioDTO>> findByUf(
            @RequestParam(value = "ufSigla", required = false) String ufSigla) {
        List<MunicipioDTO> dtos;
        if (ufSigla != null && !ufSigla.isBlank()) {
            dtos = repository.findByUnidadeFederativaSiglaOrderByNome(ufSigla.toUpperCase()).stream()
                    .map(m -> new MunicipioDTO(m.getId(), m.getNome(), m.getUnidadeFederativa().getSigla()))
                    .toList();
        } else {
            dtos = repository.findAllByOrderByNome().stream()
                    .map(m -> new MunicipioDTO(m.getId(), m.getNome(), m.getUnidadeFederativa().getSigla()))
                    .toList();
        }
        return ResponseEntity.ok(dtos);
    }
}
