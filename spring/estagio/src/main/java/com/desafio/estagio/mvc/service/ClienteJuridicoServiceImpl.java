package com.desafio.estagio.mvc.service;

import com.desafio.estagio.mvc.model.dto.ClienteFisicoDTO;
import com.desafio.estagio.mvc.model.dto.ClienteJuridicoDTO;
import com.desafio.estagio.mvc.model.entity.ClienteFisicoEntity;
import com.desafio.estagio.mvc.model.entity.ClienteJuridicoEntity;
import com.desafio.estagio.mvc.model.formatter.CNPJFormatter;
import com.desafio.estagio.mvc.model.mapper.ClienteJuridicoMapper;
import com.desafio.estagio.repository.ClienteJuridicoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ClienteJuridicoServiceImpl
        extends ClienteServiceImpl<ClienteJuridicoEntity, ClienteJuridicoDTO.Response, ClienteJuridicoRepository>
        implements ClienteJuridicoService {

    private final ClienteJuridicoMapper mapper;

    public ClienteJuridicoServiceImpl(ClienteJuridicoRepository repo, ClienteJuridicoMapper mapper) {
        super(repo);
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ClienteJuridicoDTO.Response create(ClienteJuridicoDTO.Request request) {
        log.debug("Creating new legal person client");

        // Validate and clean CNPJ
        String cleanedCnpj = CNPJFormatter.unformat(request.cnpj());
        if (repository.existsByCnpj(cleanedCnpj)) {
            throw new RuntimeException("CNPJ já cadastrado: " + request.cnpj());
        }

        // Map to entity
        ClienteJuridicoEntity entity = mapper.toEntity(request);

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
        ClienteJuridicoEntity saved = repository.save(entity);

        log.info("Created legal person client with ID: {}", saved.getId());

        return mapper.toResponse(saved);
    }

    @Override
    public ClienteJuridicoDTO.Response getById(Long id) {
        log.debug("Finding legal person client by ID: {}", id);

        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente jurídico com ID %d não encontrado", id)
                ));
    }

    @Override
    public List<ClienteJuridicoDTO.Response> findAll() {
        log.debug("Finding all legal person clients");

        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting legal person client with ID: {}", id);

        ClienteJuridicoEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente jurídico com ID %d não encontrado", id)
                ));

        repository.delete(entity);

        log.info("Deleted legal person client with ID: {}", id);
    }

    @Override
    @Transactional
    public ClienteJuridicoDTO.Response update(Long id, ClienteJuridicoDTO.Request request) {
        log.debug("Updating legal person client with ID: {}", id);

        ClienteJuridicoEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente jurídico com ID %d não encontrado", id)
                ));

        // Update fields using mapper
        mapper.updateEntityFromDTO(request, entity);

        // Update timestamp
        entity.setUpdatedAt(LocalDateTime.now());

        // Save to database
        ClienteJuridicoEntity saved = repository.save(entity);

        log.info("Updated legal person client with ID: {}", saved.getId());

        return mapper.toResponse(saved);
    }

    // =====================================================
    // CNPJ SEARCH METHODS
    // =====================================================

    /**
     * Find a legal person by CNPJ.
     *
     * @param cnpj The CNPJ (can be formatted or unformatted)
     * @return The client DTO
     * @throws EntityNotFoundException if client not found
     * @throws IllegalArgumentException if CNPJ is invalid
     */
    public ClienteJuridicoDTO.Response findByCnpj(String cnpj) {
        // Validate input
        if (cnpj == null || cnpj.isBlank()) {
            throw new IllegalArgumentException("CNPJ não pode ser nulo ou vazio");
        }

        // Clean the CNPJ (remove dots, dashes, slashes)
        String cleanedCnpj = CNPJFormatter.unformat(cnpj);

        // Validate length
        if (cleanedCnpj.length() != 14) {
            throw new IllegalArgumentException(
                    String.format("CNPJ %s tem %d dígitos, mas deve ter 14 dígitos",
                            cnpj, cleanedCnpj.length())
            );
        }

        log.debug("Searching for client with CNPJ: {} (cleaned: {})", cnpj, cleanedCnpj);

        // Find by cleaned CNPJ
        ClienteJuridicoEntity entity = repository.findByCnpj(cleanedCnpj)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente jurídico com CNPJ %s não encontrado", cnpj)
                ));

        // Convert to response DTO
        return mapper.toResponse(entity);
    }

    /**
     * Alternative: Find by CNPJ returning Optional (doesn't throw exception).
     *
     * @param cnpj The CNPJ (can be formatted or unformatted)
     * @return Optional containing the client DTO if found
     */
    public java.util.Optional<ClienteJuridicoDTO.Response> findByCnpjOptional(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            return java.util.Optional.empty();
        }

        String cleanedCnpj = CNPJFormatter.unformat(cnpj);

        if (cleanedCnpj.length() != 14) {
            return java.util.Optional.empty();
        }

        return repository.findByCnpj(cleanedCnpj)
                .map(mapper::toResponse);
    }

    /**
     * Check if a CNPJ already exists.
     *
     * @param cnpj The CNPJ to check
     * @return true if exists, false otherwise
     */
    public boolean existsByCnpj(String cnpj) {
        if (cnpj == null) return false;

        String cleanedCnpj = CNPJFormatter.unformat(cnpj);
        return repository.existsByCnpj(cleanedCnpj);
    }

    // =====================================================
    // ACTIVATION/INACTIVATION METHODS
    // =====================================================

    @Override
    @Transactional
    public void inativarCliente(Long id) {
        log.debug("Inactivating legal person client with ID: {}", id);

        ClienteJuridicoEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente jurídico com ID %d não encontrado", id)
                ));

        entity.setEstaAtivo(false);
        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);

        log.info("Inactivated legal person client with ID: {}", id);
    }

    @Transactional
    public void ativarCliente(Long id) {
        log.debug("Activating legal person client with ID: {}", id);

        ClienteJuridicoEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente jurídico com ID %d não encontrado", id)
                ));

        entity.setEstaAtivo(true);
        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);

        log.info("Activated legal person client with ID: {}", id);
    }

    // =====================================================
    // ADDITIONAL BUSINESS METHODS
    // =====================================================

    /**
     * Find all active legal person clients.
     */
    public List<ClienteJuridicoDTO.Response> findAllActive() {
        return repository.findByEstaAtivoTrue().stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * Find all inactive legal person clients.
     */
    public List<ClienteJuridicoDTO.Response> findAllInactive() {
        return repository.findByEstaAtivoFalse().stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * Get total count of legal person clients.
     */
    public long count() {
        return repository.count();
    }
}