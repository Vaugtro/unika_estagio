package com.desafio.estagio.mvc.service;

import com.desafio.estagio.mvc.model.dto.ClienteJuridicoDTO;
import com.desafio.estagio.mvc.model.entity.ClienteJuridicoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClienteJuridicoService {
    Page<ClienteJuridicoDTO.Response> findAll(Pageable pageable);

    List<ClienteJuridicoDTO.Response> findAll(); // Keep for backward compatibility

    ClienteJuridicoEntity findById(Long id);

    ClienteJuridicoDTO.Response create(ClienteJuridicoDTO.Request request);

    ClienteJuridicoDTO.Response update(Long id, ClienteJuridicoDTO.Request request);

    void delete(Long id);

    void inativarCliente(Long id);

    void ativarCliente(Long id);

    ClienteJuridicoDTO.Response findByCnpj(String cnpj);

    ClienteJuridicoDTO.Response getById(Long id);
}