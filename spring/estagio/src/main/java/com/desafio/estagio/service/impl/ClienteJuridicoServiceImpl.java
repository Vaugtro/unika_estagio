package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.clientejuridico.*;
import com.desafio.estagio.exceptions.ConflictException;
import com.desafio.estagio.mapper.ClienteJuridicoMapper;
import com.desafio.estagio.model.ClienteJuridico;
import com.desafio.estagio.model.formatter.CNPJFormatter;
import com.desafio.estagio.repository.ClienteJuridicoRepository;
import com.desafio.estagio.service.AbstractClienteService;
import com.desafio.estagio.service.ClienteJuridicoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ClienteJuridicoServiceImpl extends AbstractClienteService<ClienteJuridico, ClienteJuridicoRepository>
        implements ClienteJuridicoService {

    private final ClienteJuridicoMapper mapper;

    public ClienteJuridicoServiceImpl(ClienteJuridicoRepository repository, ClienteJuridicoMapper mapper) {
        super(repository);
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ClienteJuridicoResponse create(ClienteJuridicoCreateRequest request) {
        log.debug("Creating ClienteJuridico with CNPJ: {}", request.cnpj());
        validateCnpjUniqueness(request.cnpj());

        ClienteJuridico model = mapper.toEntity(request);
        ClienteJuridico savedModel = repository.save(model);

        log.info("Created ClienteJuridico with ID: {}", savedModel.getId());
        return mapper.toResponse(savedModel);
    }

    @Override
    @Transactional
    public ClienteJuridicoResponse update(Long id, ClienteJuridicoUpdateRequest request) {
        log.debug("Updating ClienteJuridico with ID: {}", id);
        ClienteJuridico model = findModelById(id);
        ensureIsActive(model);
        mapper.updateEntity(request, model);
        ClienteJuridico updatedModel = repository.save(model);
        log.info("Updated ClienteJuridico with ID: {}", id);
        return mapper.toResponse(updatedModel);
    }

    @Override
    public ClienteJuridicoResponse findById(Long id) {
        log.debug("Finding ClienteJuridico by ID: {}", id);
        return mapper.toResponse(findModelById(id));
    }

    @Override
    public ClienteJuridicoListResponse findByIdList(Long id) {
        return mapper.toListResponse(findModelById(id));
    }

    @Override
    public Page<ClienteJuridicoListResponse> findAllActive(Pageable pageable) {
        return repository.findByEstaAtivoTrue(pageable)
                .map(mapper::toListResponse);
    }

    @Override
    public Page<ClienteJuridicoListResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toListResponse);
    }

    @Override
    public List<ClienteJuridicoResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public Page<ClienteJuridicoReportResponse> findAllForReport(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toReportResponse);
    }

    @Override
    public boolean existsByCnpj(String cnpj) {
        return repository.existsByCnpj(CNPJFormatter.unformat(cnpj));
    }

    @Override
    public ClienteJuridicoResponse findByCnpj(String cnpj) {
        String cleanedCnpj = CNPJFormatter.unformat(cnpj);
        return repository.findByCnpj(cleanedCnpj)
                .map(mapper::toResponse)
                .orElseThrow(() -> new com.desafio.estagio.exceptions.ResourceNotFoundException(
                        "ClienteJuridico não encontrado com o CNPJ: " + cnpj));
    }

    @Override
    protected String getEntityName() {
        return "ClienteJuridico";
    }

    private void validateCnpjUniqueness(String cnpj) {
        String cleanedCnpj = CNPJFormatter.unformat(cnpj);
        if (repository.existsByCnpj(cleanedCnpj)) {
            throw new ConflictException("Já existe um cliente cadastrado com este CNPJ.");
        }
    }
}
