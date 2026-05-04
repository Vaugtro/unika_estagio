package com.desafio.estagio.mvc.model.service;

import com.desafio.estagio.mvc.model.dto.ClienteFisicoDTO;
import com.desafio.estagio.mvc.model.entity.ClienteFisicoEntity;
import com.desafio.estagio.mvc.model.mapper.ClienteFisicoMapper;
import com.desafio.estagio.repository.ClienteFisicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteFisicoServiceImpl
        extends ClienteServiceImpl<ClienteFisicoEntity, ClienteFisicoDTO.Response, ClienteFisicoRepository>
        implements ClienteFisicoService {

    private final ClienteFisicoMapper mapper;

    public ClienteFisicoServiceImpl(ClienteFisicoRepository repo, ClienteFisicoMapper mapper) {
        // repo is passed to ClienteServiceImpl and becomes 'repository'
        super(repo);
        this.mapper = mapper;
    }

    @Override
    public ClienteFisicoDTO.Response create(ClienteFisicoDTO.Request request) {
        ClienteFisicoEntity entity = mapper.toEntity(request);
        // No cast needed if repository is typed correctly in the base class
        ClienteFisicoEntity saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public ClienteFisicoDTO.Response getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Cliente físico não encontrado"));
    }

    @Override
    public List<ClienteFisicoDTO.Response> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList(); // Modern Java 16+ syntax
    }

    @Override
    public ClienteFisicoDTO.Response update(Long id, ClienteFisicoDTO.Request request) {
        // Use findEntityById if it's defined in your base ClienteServiceImpl
        ClienteFisicoEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        mapper.updateEntityFromDTO(request, entity);

        return mapper.toResponse(repository.save(entity));
    }
}

