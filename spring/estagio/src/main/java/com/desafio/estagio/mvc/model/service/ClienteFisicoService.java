package com.desafio.estagio.mvc.model.service;

import com.desafio.estagio.mvc.model.dto.ClienteFisicoDTOS;
import com.desafio.estagio.mvc.model.entity.IClienteFisico;
import com.desafio.estagio.repository.ClienteFisicoRepository;
import jakarta.validation.Valid;

public interface ClienteFisicoService extends ClienteService<IClienteFisico, ClienteFisicoRepository> {

    ClienteFisicoDTOS.Response create(ClienteFisicoDTOS.Request request);
}
