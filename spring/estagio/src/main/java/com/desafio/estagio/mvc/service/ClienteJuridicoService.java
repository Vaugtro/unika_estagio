package com.desafio.estagio.mvc.service;

import com.desafio.estagio.mvc.model.dto.ClienteJuridicoDTO;
import com.desafio.estagio.mvc.model.entity.ClienteJuridicoEntity;

import java.util.List;

public interface ClienteJuridicoService {

    ClienteJuridicoDTO.Response create(ClienteJuridicoDTO.Request request);

    ClienteJuridicoDTO.Response getById(Long id);

    List<ClienteJuridicoDTO.Response> findAll();

    ClienteJuridicoDTO.Response update(Long id, ClienteJuridicoDTO.Request request);

    void delete(Long id);

    void inativarCliente(Long id);

    void ativarCliente(Long id);

    // CNPJ specific methods
    ClienteJuridicoDTO.Response findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);

    ClienteJuridicoEntity findById(Long id);
}