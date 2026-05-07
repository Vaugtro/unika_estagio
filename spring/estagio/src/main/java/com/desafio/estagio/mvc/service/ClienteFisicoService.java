package com.desafio.estagio.mvc.service;

import com.desafio.estagio.mvc.model.dto.ClienteFisicoDTO;
import com.desafio.estagio.mvc.model.entity.ClienteFisicoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClienteFisicoService {
    Page<ClienteFisicoDTO.Response> findAll(Pageable pageable);

    List<ClienteFisicoDTO.Response> findAll(); // Keep for backward compatibility

    ClienteFisicoEntity findById(Long id);

    ClienteFisicoDTO.Response create(ClienteFisicoDTO.Request request);

    ClienteFisicoDTO.Response update(Long id, ClienteFisicoDTO.Request request);

    void delete(Long id);

    void inativarCliente(Long id);

    void ativarCliente(Long id);

    ClienteFisicoDTO.Response findByCpf(String cpf);

    ClienteFisicoDTO.Response getById(Long id);
}