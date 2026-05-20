package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.clientefisico.*;
import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.exceptions.ConflictException;
import com.desafio.estagio.exceptions.ResourceNotFoundException;
import com.desafio.estagio.mapper.ClienteFisicoMapper;
import com.desafio.estagio.model.ClienteFisico;
import com.desafio.estagio.repository.ClienteFisicoRepository;
import com.desafio.estagio.service.AbstractClienteService;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.service.EnderecoService;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ClienteFisicoServiceImpl extends AbstractClienteService<ClienteFisico, ClienteFisicoRepository>
        implements ClienteFisicoService {

    private final ClienteFisicoMapper mapper;
    private final EnderecoService enderecoService;
    private final EntityManager entityManager;

    public ClienteFisicoServiceImpl(ClienteFisicoRepository repository, ClienteFisicoMapper mapper,
                                    EnderecoService enderecoService, EntityManager entityManager) {
        super(repository);
        this.mapper = mapper;
        this.enderecoService = enderecoService;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public ClienteFisicoResponse create(ClienteFisicoCreateRequest request) {
        log.debug("Creating ClienteFisico with CPF: {}", request.cpf());

        validateCpfUniqueness(request.cpf());

        if (request.enderecos() == null || request.enderecos().isEmpty()) {
            throw new BusinessException("Cliente deve ter pelo menos um endereço cadastrado.");
        }

        boolean hasPrincipal = request.enderecos().stream()
                .anyMatch(endereco -> Boolean.TRUE.equals(endereco.principal()));

        if (!hasPrincipal) {
            throw new BusinessException("Cliente deve ter pelo menos um endereço marcado como principal.");
        }

        ClienteFisico model = mapper.toEntity(request);
        ClienteFisico savedModel = repository.save(model);

        request.enderecos().forEach(enderecoRequest ->
                enderecoService.createForCliente(savedModel.getId(), enderecoRequest));

        log.info("Created ClienteFisico with ID: {} and {} endereços", savedModel.getId(), request.enderecos().size());
        return mapper.toResponse(savedModel);
    }

    @Override
    @Transactional
    public ClienteFisicoResponse update(Long id, ClienteFisicoUpdateRequest request) {
        log.debug("Updating ClienteFisico with ID: {}", id);
        ClienteFisico model = findModelById(id);
        ensureIsActive(model);
        mapper.updateEntity(request, model);
        ClienteFisico updatedModel = repository.save(model);
        log.info("Updated ClienteFisico with ID: {}", id);
        return mapper.toResponse(updatedModel);
    }

    @Override
    @Transactional
    @CacheEvict(value = "clientes", key = "#id")
    public void activate(Long id) {
        log.debug("Activating ClienteFisico with ID: {}", id);
        ClienteFisico model = findModelById(id);
        if (Boolean.TRUE.equals(model.getEstaAtivo())) {
            throw new BusinessException("Este cliente já está ativo.");
        }
        model.setEstaAtivo(true);
        repository.save(model);
        entityManager.flush();
        entityManager.detach(model);
        log.info("Activated ClienteFisico with ID: {}", id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "clientes", key = "#id")
    public void inactivate(Long id) {
        log.debug("Inactivating ClienteFisico with ID: {}", id);
        ClienteFisico model = findModelById(id);
        if (Boolean.FALSE.equals(model.getEstaAtivo())) {
            throw new BusinessException("Este cliente já está inativo.");
        }
        model.setEstaAtivo(false);
        repository.save(model);
        entityManager.flush();
        entityManager.detach(model);
        log.info("Inactivated ClienteFisico with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteFisicoResponse findById(Long id) {
        entityManager.clear();
        return mapper.toResponse(findModelById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteFisicoListResponse findByIdList(Long id) {
        return mapper.toListResponse(findModelById(id));
    }

    @Override
    public Page<ClienteFisicoListResponse> findAllActive(Pageable pageable) {
        return repository.findByEstaAtivoTrue(pageable)
                .map(mapper::toListResponse);
    }

    @Override
    public Page<ClienteFisicoListResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toListResponse);
    }

    @Override
    public Page<ClienteFisicoReportResponse> findAllForReport(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toReportResponse);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return repository.existsByCpf(cpf);
    }

    @Override
    public ClienteFisicoResponse findByCpf(String cpf) {
        return repository.findByCpf(cpf)
                .map(mapper::toResponse)
                .orElseThrow(() -> new com.desafio.estagio.exceptions.ResourceNotFoundException(
                        "Cliente não encontrado com o CPF: " + cpf));
    }

    @Override
    protected String getEntityName() {
        return "ClienteFisico";
    }

    private void validateCpfUniqueness(String cpf) {
        if (repository.existsByCpf(cpf)) {
            throw new ConflictException("Já existe um cliente cadastrado com este CPF.");
        }
    }
}
