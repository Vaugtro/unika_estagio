package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.clientefisico.*;
import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.exceptions.ConflictException;
import com.desafio.estagio.mapper.ClienteFisicoMapper;
import com.desafio.estagio.model.ClienteFisico;
import com.desafio.estagio.repository.ClienteFisicoRepository;
import com.desafio.estagio.service.AbstractClienteService;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.service.EnderecoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

    public ClienteFisicoServiceImpl(ClienteFisicoRepository repository, ClienteFisicoMapper mapper,
                                    EnderecoService enderecoService) {
        super(repository);
        this.mapper = mapper;
        this.enderecoService = enderecoService;
    }

    @Override
    @Transactional
    public ClienteFisicoResponse create(@Valid ClienteFisicoCreateRequest request) {
        log.debug("Creating ClienteFisico with CPF: {}", request.cpf());

        String cleanedCpf = request.cpf() != null ? request.cpf().replaceAll("\\D", "") : null;
        validateCpfUniqueness(cleanedCpf);

        if (request.enderecos() == null || request.enderecos().isEmpty()) {
            throw new BusinessException("Cliente deve ter pelo menos um endereço cadastrado.");
        }

        boolean hasPrincipal = request.enderecos().stream()
                .anyMatch(endereco -> Boolean.TRUE.equals(endereco.principal()));

        if (!hasPrincipal) {
            throw new BusinessException("Cliente deve ter pelo menos um endereço marcado como principal.");
        }

        ClienteFisicoCreateRequest sanitizedRequest = new ClienteFisicoCreateRequest(
                cleanedCpf,
                request.nome(),
                request.rg(),
                request.email(),
                request.dataNascimento(),
                request.enderecos()
        );

        ClienteFisico model = mapper.toEntity(sanitizedRequest);
        ClienteFisico savedModel = repository.save(model);

        request.enderecos().forEach(enderecoRequest ->
                enderecoService.createForCliente(savedModel.getId(), enderecoRequest));

        log.info("Created ClienteFisico with ID: {} and {} endereços", savedModel.getId(), request.enderecos().size());
        return mapper.toResponse(savedModel);
    }

    @Override
    @Transactional
    public ClienteFisicoResponse update(Long id, @Valid ClienteFisicoUpdateRequest request) {
        log.debug("Updating ClienteFisico with ID: {}", id);
        ClienteFisico model = findModelById(id);
        ensureIsActive(model);
        mapper.updateEntity(request, model);
        ClienteFisico updatedModel = repository.save(model);
        log.info("Updated ClienteFisico with ID: {}", id);
        return mapper.toResponse(updatedModel);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteFisicoResponse findById(Long id) {
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
