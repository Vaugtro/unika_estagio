package com.desafio.estagio.mvc.model.service;

import com.desafio.estagio.mvc.model.dto.ClienteJuridicoDTO;
import com.desafio.estagio.mvc.model.entity.ClienteJuridicoEntity;
import com.desafio.estagio.mvc.model.mapper.ClienteJuridicoMapper;
import com.desafio.estagio.repository.ClienteJuridicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteJuridicoServiceImpl
        extends ClienteServiceImpl<ClienteJuridicoEntity, ClienteJuridicoDTO.Response, ClienteJuridicoRepository>
        implements ClienteJuridicoService {

    private final ClienteJuridicoMapper mapper;

    public ClienteJuridicoServiceImpl(ClienteJuridicoRepository repo, ClienteJuridicoMapper mapper) {
        // repo is passed to ClienteServiceImpl and becomes 'repository'
        super(repo);
        this.mapper = mapper;
    }

    @Override
    public ClienteJuridicoDTO.Response create(ClienteJuridicoDTO.Request request) {
        ClienteJuridicoEntity entity = mapper.toEntity(request);
        // No cast needed if repository is typed correctly in the base class
        ClienteJuridicoEntity saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public ClienteJuridicoDTO.Response getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Cliente físico não encontrado"));
    }

    @Override
    public List<ClienteJuridicoDTO.Response> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList(); // Modern Java 16+ syntax
    }

    @Override
    public ClienteJuridicoDTO.Response update(Long id, ClienteJuridicoDTO.Request request) {
        // Use findEntityById if it's defined in your base ClienteServiceImpl
        ClienteJuridicoEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        mapper.updateEntityFromDTO(request, entity);

        return mapper.toResponse(repository.save(entity));
    }
}

