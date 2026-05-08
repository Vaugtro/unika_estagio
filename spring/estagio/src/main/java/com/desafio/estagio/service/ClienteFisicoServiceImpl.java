package com.desafio.estagio.service;

import com.desafio.estagio.dto.ClienteFisicoDTO;
import com.desafio.estagio.mapper.ClienteFisicoMapper;
import com.desafio.estagio.model.ClienteFisicoEntity;
import com.desafio.estagio.model.enums.TipoCliente;
import com.desafio.estagio.model.formatter.CPFFormatter;
import com.desafio.estagio.repository.ClienteFisicoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // Helper method to create response with formatted CPF
    private ClienteFisicoDTO.Response toResponseWithFormattedCpf(ClienteFisicoEntity entity) {
        ClienteFisicoDTO.Response response = mapper.toResponse(entity);
        return new ClienteFisicoDTO.Response(
                response.id(),
                response.tipo(),
                response.email(),
                CPFFormatter.format(response.cpf()),  // Formatted CPF
                response.nome(),
                response.rg(),
                response.estaAtivo(),
                response.dataNascimento(),
                response.enderecos(),
                response.createdAt(),
                response.updatedAt()
        );
    }

    @Override
    public ClienteFisicoDTO.Response getById(Long id) {
        ClienteFisicoEntity entity = findById(id);
        return mapper.toResponse(entity);
    }

    @Override
    public Page<ClienteFisicoDTO.Response> findAll(Pageable pageable) {
        log.debug("Finding all physical person clients with pagination");
        Page<ClienteFisicoEntity> entities = repository.findAll(pageable);
        return entities.map(this::toResponseWithFormattedCpf);
    }

    @Override
    public List<ClienteFisicoDTO.Response> findAll() {
        log.debug("Finding all physical person clients");

        return repository.findAll()
                .stream()
                .map(this::toResponseWithFormattedCpf)
                .toList();
    }

    @Override
    public ClienteFisicoDTO.Response findByCpf(String cpf) {
        // Clean CPF in service layer
        String cleanedCpf = CPFFormatter.unformat(cpf);

        if (cleanedCpf == null || cleanedCpf.isBlank()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio");
        }

        if (cleanedCpf.length() != 11) {
            throw new IllegalArgumentException(
                    String.format("CPF %s tem %d dígitos, mas deve ter 11 dígitos", cpf, cleanedCpf.length())
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

        // Clean CPF
        String cleanedCpf = CPFFormatter.unformat(request.cpf());

        // Check for duplicate CPF
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
        entity.setCpf(cleanedCpf);  // Set cleaned CPF
        entity.setRg(cleanedRg);     // Set cleaned RG

        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        // Set default active status if not provided
        if (entity.getEstaAtivo() == null) {
            entity.setEstaAtivo(true);
        }

        // Set discriminator type
        entity.setTipo(TipoCliente.FISICA);

        // Link enderecos to cliente
        if (entity.getEnderecos() != null) {
            entity.getEnderecos().forEach(endereco -> endereco.setCliente(entity));
        }

        // Save to database
        ClienteFisicoEntity saved = repository.save(entity);

        log.info("Created physical person client with ID: {}", saved.getId());

        return toResponseWithFormattedCpf(saved);
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
                // Check for duplicate RG if changed
                if (!cleanedRg.equals(entity.getRg()) && repository.existsByRg(cleanedRg)) {
                    throw new DataIntegrityViolationException("RG já cadastrado: " + request.rg());
                }
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

        return toResponseWithFormattedCpf(saved);
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