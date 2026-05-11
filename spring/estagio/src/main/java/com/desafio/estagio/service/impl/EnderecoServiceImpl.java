package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.EnderecoDTO;
import com.desafio.estagio.mapper.EnderecoMapper;
import com.desafio.estagio.model.ClienteEntity;
import com.desafio.estagio.model.EnderecoEntity;
import com.desafio.estagio.repository.ClienteRepository;
import com.desafio.estagio.repository.EnderecoRepository;
import com.desafio.estagio.service.EnderecoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnderecoServiceImpl implements EnderecoService {

    private final EnderecoRepository<EnderecoEntity> enderecoRepository;
    private final ClienteRepository<ClienteEntity> clienteRepository;
    private final EnderecoMapper enderecoMapper;

    public EnderecoServiceImpl(EnderecoRepository<EnderecoEntity> enderecoRepository,
                               ClienteRepository<ClienteEntity> clienteRepository,
                               EnderecoMapper enderecoMapper) {
        this.enderecoRepository = enderecoRepository;
        this.clienteRepository = clienteRepository;
        this.enderecoMapper = enderecoMapper;
    }

    @Override
    public EnderecoDTO.Response create(EnderecoDTO.Request request) {
        EnderecoEntity entity = enderecoMapper.toEntity(request);

        if (Boolean.TRUE.equals(request.principal())) {
            if (entity.getCliente() != null) {
                removePrincipalFlagFromOtherAddresses(entity.getCliente().getId(), null);
            }
        }

        EnderecoEntity saved = enderecoRepository.save(entity);
        return enderecoMapper.toResponse(saved);
    }

    @Override
    public EnderecoDTO.Response createForCliente(Long clienteId, EnderecoDTO.Request request) {
        ClienteEntity cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + clienteId));

        EnderecoEntity entity = enderecoMapper.toEntity(request);
        entity.setCliente(cliente);

        long addressCount = enderecoRepository.countByClienteId(clienteId);
        boolean hasNoEnderecos = addressCount == 0;

        if (request.principal() != null && request.principal()) {
            removePrincipalFlagFromOtherAddresses(clienteId, null);
            entity.setPrincipal(true);
        } else entity.setPrincipal(hasNoEnderecos);

        EnderecoEntity saved = enderecoRepository.save(entity);
        return enderecoMapper.toResponse(saved);
    }

    @Override
    public EnderecoDTO.Response findById(Long id) {
        EnderecoEntity entity = findEntityById(id);
        return enderecoMapper.toResponse(entity);
    }

    @Override
    public List<EnderecoDTO.Response> findAllByClienteId(Long clienteId) {
        List<EnderecoEntity> enderecos = enderecoRepository.findByClienteId(clienteId);
        if (enderecos == null) {
            return List.of();
        }
        return enderecos.stream()
                .map(enderecoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnderecoDTO.Response> findAll() {
        List<EnderecoEntity> enderecos = enderecoRepository.findAll();
        return enderecos.stream()
                .map(enderecoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EnderecoDTO.Response update(Long id, EnderecoDTO.Request request) {
        EnderecoEntity existing = findEntityById(id);
        enderecoMapper.updateEntityFromDTO(request, existing);

        // Handle principal flag change
        if (Boolean.TRUE.equals(request.principal()) && !existing.getPrincipal()) {
            removePrincipalFlagFromOtherAddresses(existing.getCliente().getId(), id);
            existing.setPrincipal(true);
        }

        EnderecoEntity updated = enderecoRepository.save(existing);
        return enderecoMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public EnderecoDTO.Response setAsPrincipal(Long id) {
        EnderecoEntity endereco = findEntityById(id);
        Long clienteId = endereco.getCliente().getId();

        removePrincipalFlagFromOtherAddresses(clienteId, id);

        endereco.setPrincipal(true);
        EnderecoEntity updated = enderecoRepository.save(endereco);

        return enderecoMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        EnderecoEntity endereco = findEntityById(id);
        Long clienteId = endereco.getCliente().getId();

        if (endereco.getPrincipal()) {
            List<EnderecoEntity> otherAddresses = enderecoRepository.findByClienteId(clienteId)
                    .stream()
                    .filter(e -> !e.getId().equals(id))
                    .toList();

            if (!otherAddresses.isEmpty()) {
                otherAddresses.get(0).setPrincipal(true);
                enderecoRepository.save(otherAddresses.get(0));
            }
        }

        enderecoRepository.deleteById(id);
    }

    @Override
    public EnderecoDTO.Response findPrincipalEnderecoByClienteId(Long clienteId) {
        EnderecoEntity principal = enderecoRepository.findByClienteIdAndPrincipalTrue(clienteId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Endereço principal não encontrado para o cliente ID: " + clienteId));
        return enderecoMapper.toResponse(principal);
    }

    private EnderecoEntity findEntityById(Long id) {
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado com ID: " + id));
    }

    private void removePrincipalFlagFromOtherAddresses(Long clienteId, Long excludeEnderecoId) {
        List<EnderecoEntity> enderecos = enderecoRepository.findByClienteId(clienteId);
        if (enderecos == null || enderecos.isEmpty()) {
            return;
        }

        for (EnderecoEntity endereco : enderecos) {
            if (!endereco.getId().equals(excludeEnderecoId)) {
                endereco.setPrincipal(false);
                enderecoRepository.save(endereco);
            }
        }
    }
}