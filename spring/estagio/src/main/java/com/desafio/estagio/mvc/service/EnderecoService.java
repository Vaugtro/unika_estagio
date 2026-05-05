package com.desafio.estagio.mvc.service;

import com.desafio.estagio.mvc.model.dto.EnderecoDTO;

import java.util.List;

public interface EnderecoService {

    EnderecoDTO.Response create(EnderecoDTO.Request request);

    EnderecoDTO.Response createForCliente(Long clienteId, EnderecoDTO.Request request);

    EnderecoDTO.Response findById(Long id);

    List<EnderecoDTO.Response> findAllByClienteId(Long clienteId);

    List<EnderecoDTO.Response> findAll();

    EnderecoDTO.Response update(Long id, EnderecoDTO.Request request);

    EnderecoDTO.Response setAsPrincipal(Long id);

    void delete(Long id);

    EnderecoDTO.Response findPrincipalEnderecoByClienteId(Long clienteId);
}