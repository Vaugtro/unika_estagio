package com.desafio.estagio.mvc.service;

import com.desafio.estagio.mvc.model.dto.ClienteFisicoDTO;
import com.desafio.estagio.mvc.model.entity.ClienteFisicoEntity;
import com.desafio.estagio.mvc.model.formatter.CPFFormatter;
import com.desafio.estagio.mvc.model.mapper.ClienteFisicoMapper;
import com.desafio.estagio.repository.ClienteFisicoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ClienteFisicoServiceImpl
        extends ClienteServiceImpl<ClienteFisicoEntity, ClienteFisicoDTO.Response, ClienteFisicoRepository>
        implements ClienteFisicoService {

    private final ClienteFisicoMapper mapper;

    public ClienteFisicoServiceImpl(ClienteFisicoRepository repo, ClienteFisicoMapper mapper) {
        super(repo);
        this.mapper = mapper;
    }

    @Override
    public ClienteFisicoDTO.Response getById(Long id) {
        return null;
    }

    @Override
    public List<ClienteFisicoDTO.Response> findAll() {
        log.debug("Finding all physical person clients");

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public ClienteFisicoDTO.Response findByCpf(String cpf) {
        // Validate input
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio");
        }

        String cleanedCpf = CPFFormatter.unformat(cpf);

        // Validate length
        if (cleanedCpf.length() != 11) {
            throw new IllegalArgumentException(
                    String.format("CPF %s tem %d dígitos, mas deve ter 11 dígitos",
                            cpf, cleanedCpf.length())
            );
        }

        log.debug("Searching for client with CPF: {} (cleaned: {})", cpf, cleanedCpf);

        ClienteFisicoEntity entity = repository.findByCpf(cleanedCpf)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente físico com CPF %s não encontrado", cpf)
                ));

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public ClienteFisicoDTO.Response create(ClienteFisicoDTO.Request request) {
        log.debug("Creating new physical person client");

        // Validate request
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        // Check for duplicate CPF
        String cleanedCpf = CPFFormatter.unformat(request.cpf());
        if (repository.existsByCpf(cleanedCpf)) {
            throw new DataIntegrityViolationException("CPF já cadastrado: " + request.cpf());
        }

        // Check for duplicate RG
        String cleanedRg = request.rg() != null ? request.rg().replaceAll("\\D", "") : null;
        if (cleanedRg != null && repository.existsByRg(cleanedRg)) {
            throw new DataIntegrityViolationException("RG já cadastrado: " + request.rg());
        }

        // Map to entity
        ClienteFisicoEntity entity = mapper.toEntity(request);

        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        // Set default active status if not provided
        if (entity.getEstaAtivo() == null) {
            entity.setEstaAtivo(true);
        }

        // Link enderecos to cliente
        if (entity.getEnderecos() != null) {
            entity.getEnderecos().forEach(endereco -> endereco.setCliente(entity));
        }

        // Save to database
        ClienteFisicoEntity saved = repository.save(entity);

        log.info("Created physical person client with ID: {}", saved.getId());

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ClienteFisicoDTO.Response update(Long id, ClienteFisicoDTO.Request request) {
        log.debug("Updating physical person client with ID: {}", id);

        ClienteFisicoEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente físico com ID %d não encontrado", id)
                ));

        // Update fields (partial update)
        if (request.email() != null) {
            entity.setEmail(request.email());
        }
        if (request.nome() != null) {
            entity.setNome(request.nome());
        }
        if (request.rg() != null) {
            String cleanedRg = request.rg().replaceAll("\\D", "");
            if (cleanedRg.length() >= 8 && cleanedRg.length() <= 9) {
                entity.setRg(cleanedRg);
            }
        }
        if (request.estaAtivo() != null) {
            entity.setEstaAtivo(request.estaAtivo());
        }
        if (request.dataNascimento() != null) {
            entity.setDataNascimento(request.dataNascimento());
        }

        entity.setUpdatedAt(LocalDateTime.now());

        ClienteFisicoEntity saved = repository.save(entity);

        log.info("Updated physical person client with ID: {}", saved.getId());

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void inativarCliente(Long id) {
        log.debug("Inactivating physical person client with ID: {}", id);

        ClienteFisicoEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente físico com ID %d não encontrado", id)
                ));

        entity.setEstaAtivo(false);
        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);

        log.info("Inactivated physical person client with ID: {}", id);
    }

    @Override
    @Transactional
    public void ativarCliente(Long id) {
        log.debug("Activating physical person client with ID: {}", id);

        ClienteFisicoEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente físico com ID %d não encontrado", id)
                ));

        entity.setEstaAtivo(true);
        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);

        log.info("Activated physical person client with ID: {}", id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting physical person client with ID: {}", id);

        ClienteFisicoEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente físico com ID %d não encontrado", id)
                ));

        repository.delete(entity);

        log.info("Deleted physical person client with ID: {}", id);
    }
}