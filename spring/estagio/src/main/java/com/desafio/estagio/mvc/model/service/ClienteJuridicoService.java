package com.desafio.estagio.mvc.model.service;

import com.desafio.estagio.mvc.model.dto.ClienteJuridicoDTO;
import com.desafio.estagio.mvc.model.entity.ClienteJuridicoEntity;
import com.desafio.estagio.repository.ClienteJuridicoRepository;

import java.util.List;

public interface ClienteJuridicoService
        extends ClienteService<ClienteJuridicoEntity, ClienteJuridicoDTO.Response, ClienteJuridicoRepository> {

    ClienteJuridicoDTO.Response getById(Long id);

    List<ClienteJuridicoDTO.Response> findAll();

    // Specific methods that the generic service doesn't know about
    ClienteJuridicoDTO.Response create(ClienteJuridicoDTO.Request request);

    ClienteJuridicoDTO.Response update(Long id, ClienteJuridicoDTO.Request request);
}