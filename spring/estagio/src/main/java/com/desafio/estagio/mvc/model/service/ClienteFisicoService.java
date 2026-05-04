package com.desafio.estagio.mvc.model.service;

import com.desafio.estagio.mvc.model.dto.ClienteDTO;
import com.desafio.estagio.mvc.model.dto.ClienteFisicoDTO;
import com.desafio.estagio.mvc.model.entity.ClienteFisicoEntity;
import com.desafio.estagio.repository.ClienteFisicoRepository;

import java.util.List;

public interface ClienteFisicoService
        extends ClienteService<ClienteFisicoEntity, ClienteFisicoDTO.Response, ClienteFisicoRepository> {

    ClienteFisicoDTO.Response getById(Long id);

    List<ClienteFisicoDTO.Response> findAll();

    // Specific methods that the generic service doesn't know about
    ClienteFisicoDTO.Response create(ClienteFisicoDTO.Request request);

    ClienteFisicoDTO.Response update(Long id, ClienteFisicoDTO.Request request);
}