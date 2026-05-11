package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.ClienteFisicoDTO;
import com.desafio.estagio.model.ClienteFisicoEntity;
import com.desafio.estagio.repository.ClienteFisicoRepository;
import org.springframework.stereotype.Service;

/**
 * Service implementation for ClienteFisico
 */
@Service
public class ClienteFisicoServiceImpl extends ClienteServiceImpl<ClienteFisicoEntity, ClienteFisicoDTO.Response, ClienteFisicoRepository> {

    /**
     * Constructor
     *
     * @param repository the repository instance
     */
    protected ClienteFisicoServiceImpl(ClienteFisicoRepository repository) {
        super(repository);
    }

    @Override
    protected ClienteFisicoDTO.Response toDTO(ClienteFisicoEntity entity) {
        if (entity == null) {
            return null;
        }
        return ClienteFisicoDTO.Response.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .cpf(entity.getCpf())
                .dataNascimento(entity.getDataNascimento())
                .estaAtivo(entity.getEstaAtivo())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    protected ClienteFisicoEntity toEntity(ClienteFisicoDTO.Response dto) {
        if (dto == null) {
            return null;
        }
        return ClienteFisicoEntity.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .cpf(dto.getCpf())
                .dataNascimento(dto.getDataNascimento())
                .estaAtivo(true)
                .build();
    }

    @Override
    protected ClienteFisicoEntity toEntity(Long id, ClienteFisicoDTO.Response dto, ClienteFisicoEntity existing) {
        if (dto == null) {
            return existing;
        }
        existing.setNome(dto.getNome());
        existing.setEmail(dto.getEmail());
        existing.setTelefone(dto.getTelefone());
        existing.setCpf(dto.getCpf());
        existing.setDataNascimento(dto.getDataNascimento());
        return existing;
    }
}