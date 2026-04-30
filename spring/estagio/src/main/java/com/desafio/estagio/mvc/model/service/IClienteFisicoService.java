package com.desafio.estagio.mvc.model.service;

import com.desafio.estagio.mvc.model.dto.ClienteFisicoDTOS;
import com.desafio.estagio.mvc.model.entity.ClienteFisico;
import com.desafio.estagio.mvc.model.entity.IClienteFisico;
import com.desafio.estagio.mvc.model.mapper.ClienteFisicoMapper;
import com.desafio.estagio.repository.ClienteFisicoRepository;
import org.springframework.stereotype.Service;

@Service
public class IClienteFisicoService extends IClienteService<IClienteFisico, ClienteFisicoRepository> implements ClienteFisicoService {

    private final ClienteFisicoMapper mapper;

    public IClienteFisicoService(ClienteFisicoRepository repo, ClienteFisicoMapper mapper) {
        super(repo);

        this.mapper = mapper;
    }

    public ClienteFisicoDTOS.Response create(ClienteFisicoDTOS.Request request) {
        IClienteFisico entity = mapper.toEntity(request);
        IClienteFisico saved = repository.save(entity);
        return mapper.toResponse(saved);
    }
}

