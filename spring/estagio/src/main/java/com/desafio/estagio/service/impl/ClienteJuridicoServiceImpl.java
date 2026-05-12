package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.ClienteJuridicoDTO;
import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.exceptions.ResourceNotFoundException;
import com.desafio.estagio.mapper.ClienteJuridicoMapper;
import com.desafio.estagio.model.ClienteFisico;
import com.desafio.estagio.model.ClienteJuridico;
import com.desafio.estagio.model.formatter.CNPJFormatter;
import com.desafio.estagio.repository.ClienteJuridicoRepository;
import com.desafio.estagio.service.ClienteJuridicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClienteJuridicoServiceImpl implements ClienteJuridicoService {

    private final ClienteJuridicoRepository repository;
    private final ClienteJuridicoMapper mapper;

    @Override
    @Transactional
    public ClienteJuridicoDTO.Response create(ClienteJuridicoDTO.CreateRequest request) {
        log.debug("Creating ClienteJuridico with CNPJ: {}", request.cnpj());

        validateCnpjUniqueness(request.cnpj());

        ClienteJuridico model = mapper.toEntity(request);
        ClienteJuridico savedModel = repository.save(model);

        log.info("Created ClienteJuridico with ID: {}", savedModel.getId());
        return mapper.toResponse(savedModel);
    }

    @Override
    @Transactional
    public ClienteJuridicoDTO.Response update(Long id, ClienteJuridicoDTO.UpdateRequest request) {
        log.debug("Updating ClienteJuridico with ID: {}", id);

        ClienteJuridico model = findModelById(id);
        ensureClientIsActive(model);

        mapper.updateEntity(request, model);

        ClienteJuridico updatedModel = repository.save(model);
        log.info("Updated ClienteJuridico with ID: {}", id);

        return mapper.toResponse(updatedModel);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // Standardizing delete to use the inactivate logic
        this.inactivate(id);
    }

    @Override
    @Transactional
    public void hardDelete(Long id) {
        log.debug("Hard deleting ClienteFisico with ID: {}", id);
        ClienteJuridico model = findModelById(id);
        repository.delete(model);
        log.info("Hard deleted ClienteFisico with ID: {}", id);
    }

    @Override
    @Transactional
    public void activate(Long id) {
        log.debug("Activating ClienteJuridico with ID: {}", id);

        ClienteJuridico model = findModelById(id);

        if (Boolean.TRUE.equals(model.getEstaAtivo())) {
            throw new BusinessException("Este cliente já está ativo.");
        }

        model.setEstaAtivo(true);
        repository.save(model);

        log.info("Activated ClienteJuridico with ID: {}", id);
    }

    @Override
    @Transactional
    public void inactivate(Long id) {
        log.debug("Inactivating ClienteJuridico with ID: {}", id);

        ClienteJuridico model = findModelById(id);

        if (Boolean.FALSE.equals(model.getEstaAtivo())) {
            throw new BusinessException("Este cliente já está inativo.");
        }

        model.setEstaAtivo(false);
        repository.save(model);

        log.info("Inactivated ClienteJuridico with ID: {}", id);
    }

    @Override
    public ClienteJuridicoDTO.Response findById(Long id) {
        log.debug("Finding ClienteJuridico by ID: {}", id);
        return mapper.toResponse(findModelById(id));
    }

    @Override
    public Page<ClienteJuridicoDTO.ListResponse> findAllActive(Pageable pageable) {
        log.debug("Finding all active ClienteJuridico with pagination");
        return repository.findByEstaAtivoTrue(pageable)
                .map(mapper::toListResponse);
    }

    @Override
    public Page<ClienteJuridicoDTO.ListResponse> findAll(Pageable pageable) {
        log.debug("Finding all ClienteJuridico with pagination");
        return repository.findAll(pageable)
                .map(mapper::toListResponse);
    }

    @Override
    public List<ClienteJuridicoDTO.Response> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public Page<ClienteJuridicoDTO.ReportResponse> findAllForReport(Pageable pageable) {
        log.debug("Finding all ClienteJuridico for report generation with pagination");
        return repository.findAll(pageable)
                .map(mapper::toReportResponse);
    }

    @Override
    public boolean existsByCnpj(String cnpj) {
        return repository.existsByCnpj(CNPJFormatter.unformat(cnpj));
    }

    @Override
    public ClienteJuridicoDTO.Response findByCnpj(String cnpj) {
        String cleanedCnpj = CNPJFormatter.unformat(cnpj);
        return repository.findByCnpj(cleanedCnpj)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("ClienteJuridico não encontrado com o CNPJ: " + cnpj));
    }

    // ==================== Private Helper Methods ====================

    private ClienteJuridico findModelById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClienteJuridico não encontrado com o ID: " + id));
    }

    private void ensureClientIsActive(ClienteJuridico model) {
        if (Boolean.FALSE.equals(model.getEstaAtivo())) {
            throw new BusinessException("Operação não permitida: O cliente está inativo.");
        }
    }

    private void validateCnpjUniqueness(String cnpj) {
        String cleanedCnpj = CNPJFormatter.unformat(cnpj);
        if (repository.existsByCnpj(cleanedCnpj)) {
            throw new BusinessException("Já existe um cliente cadastrado com este CNPJ.");
        }
    }
}