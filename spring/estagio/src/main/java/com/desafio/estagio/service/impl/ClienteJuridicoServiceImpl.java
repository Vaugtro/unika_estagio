package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.ClienteJuridicoDTO;
import com.desafio.estagio.mapper.ClienteJuridicoMapper;
import com.desafio.estagio.model.ClienteJuridicoEntity;
import com.desafio.estagio.model.enums.TipoCliente;
import com.desafio.estagio.model.formatter.CNPJFormatter;
import com.desafio.estagio.repository.ClienteJuridicoRepository;
import com.desafio.estagio.service.ClienteJuridicoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // Helper method to create response with formatted CNPJ
    private ClienteJuridicoDTO.Response toResponseWithFormattedCnpj(ClienteJuridicoEntity entity) {
        ClienteJuridicoDTO.Response response = mapper.toResponse(entity);
        return new ClienteJuridicoDTO.Response(
                response.id(),
                response.tipo(),
                response.email(),
                CNPJFormatter.format(response.cnpj()),  // Formatted CNPJ
                response.razaoSocial(),
                response.inscricaoEstadual(),
                response.estaAtivo(),
                response.dataCriacaoEmpresa(),
                response.enderecos(),
                response.createdAt(),
                response.updatedAt()
        );
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
        entity.setCnpj(cleanedCnpj);  // Set cleaned CNPJ

        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        // Set default active status if not provided
        if (entity.getEstaAtivo() == null) {
            entity.setEstaAtivo(true);
        }

        // Set discriminator type
        entity.setTipo(TipoCliente.JURIDICA);

        // Link enderecos to cliente
        if (entity.getEnderecos() != null) {
            entity.getEnderecos().forEach(endereco -> endereco.setCliente(entity));
        }

        // Save to database
        ClienteJuridicoEntity saved = repository.save(entity);

        log.info("Created legal person client with ID: {}", saved.getId());

        return toResponseWithFormattedCnpj(saved);
    }

    public ClienteJuridicoDTO.Response getById(Long id) {
        log.debug("Finding legal person client by ID: {}", id);

        ClienteJuridicoEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente jurídico com ID %d não encontrado", id)
                ));

        return toResponseWithFormattedCnpj(entity);
    }

    @Override
    public Page<ClienteJuridicoDTO.Response> findAll(Pageable pageable) {
        log.debug("Finding all legal person clients with pagination: page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<ClienteJuridicoEntity> entityPage = repository.findAll(pageable);

        return entityPage.map(this::toResponseWithFormattedCnpj);
    }

    // Non-paginated version if needed
    public List<ClienteJuridicoDTO.Response> findAllList() {
        log.debug("Finding all legal person clients (non-paginated)");

        return repository.findAll().stream()
                .map(this::toResponseWithFormattedCnpj)
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

        // If CNPJ is being updated, clean it
        if (request.cnpj() != null) {
            entity.setCnpj(CNPJFormatter.unformat(request.cnpj()));
        }

        // Update timestamp
        entity.setUpdatedAt(LocalDateTime.now());

        // Save to database
        ClienteJuridicoEntity saved = repository.save(entity);

        log.info("Updated legal person client with ID: {}", saved.getId());

        return toResponseWithFormattedCnpj(saved);
    }

    // =====================================================
    // CNPJ SEARCH METHODS
    // =====================================================

    public ClienteJuridicoDTO.Response findByCnpj(String cnpj) {

        String cleanedCnpj = CNPJFormatter.unformat(cnpj);

        if (cnpj == null || cnpj.isBlank()) {
            throw new IllegalArgumentException("CNPJ não pode ser nulo ou vazio");
        }

        if (cleanedCnpj.length() != 14) {
            throw new IllegalArgumentException(
                    String.format("CNPJ %s tem %d dígitos, mas deve ter 14 dígitos",
                            cnpj, cleanedCnpj.length())
            );
        }

        log.debug("Searching for client with CNPJ: {} (cleaned: {})", cnpj, cleanedCnpj);

        ClienteJuridicoEntity entity = repository.findByCnpj(cleanedCnpj)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente jurídico com CNPJ %s não encontrado", cnpj)
                ));

        return toResponseWithFormattedCnpj(entity);
    }

    public java.util.Optional<ClienteJuridicoDTO.Response> findByCnpjOptional(String cnpj) {
        String cleanedCnpj = CNPJFormatter.unformat(cnpj);

        if (cnpj == null || cnpj.isBlank()) {
            return java.util.Optional.empty();
        }

        if (cleanedCnpj.length() != 14) {
            return java.util.Optional.empty();
        }

        return repository.findByCnpj(cleanedCnpj)
                .map(this::toResponseWithFormattedCnpj);
    }

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

    @Override
    public List<ClienteJuridicoDTO.Response> findAll() {
        log.debug("Finding all legal person clients");

        return repository.findAll()
                .stream()
                .map(this::toResponseWithFormattedCnpj)
                .toList();
    }

    // =====================================================
    // ADDITIONAL BUSINESS METHODS
    // =====================================================

    public List<ClienteJuridicoDTO.Response> findAllActive() {
        log.debug("Finding all active legal person clients");

        return repository.findByEstaAtivoTrue().stream()
                .map(this::toResponseWithFormattedCnpj)
                .toList();
    }

    public List<ClienteJuridicoDTO.Response> findAllInactive() {
        log.debug("Finding all inactive legal person clients");

        return repository.findByEstaAtivoFalse().stream()
                .map(this::toResponseWithFormattedCnpj)
                .toList();
    }

    public long count() {
        log.debug("Counting total legal person clients");

        return repository.count();
    }
}