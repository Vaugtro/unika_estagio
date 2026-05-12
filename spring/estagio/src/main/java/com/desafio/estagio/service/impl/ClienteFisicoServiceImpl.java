package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.ClienteFisicoDTO;
import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.exceptions.ResourceNotFoundException;
import com.desafio.estagio.mapper.ClienteFisicoMapper;
import com.desafio.estagio.model.ClienteFisico;
import com.desafio.estagio.repository.ClienteFisicoRepository;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.service.EnderecoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClienteFisicoServiceImpl implements ClienteFisicoService {

    private final ClienteFisicoRepository repository;
    private final ClienteFisicoMapper mapper;

    private final EnderecoService enderecoService;

    @Override
    @Transactional
    public ClienteFisicoDTO.Response create(ClienteFisicoDTO.CreateRequest request) {
        log.debug("Creating ClienteFisico with CPF: {}", request.cpf());

        // Validation
        validateCpfUniqueness(request.cpf());

        // Ensure at least one address is provided
        if (request.enderecos() == null || request.enderecos().isEmpty()) {
            throw new BusinessException("Cliente deve ter pelo menos um endereço cadastrado.");
        }

        // Ensure at least one principal address
        boolean hasPrincipal = request.enderecos().stream()
                .anyMatch(endereco -> Boolean.TRUE.equals(endereco.principal()));

        if (!hasPrincipal) {
            throw new BusinessException("Cliente deve ter pelo menos um endereço marcado como principal.");
        }

        // Create cliente
        ClienteFisico model = mapper.toEntity(request);
        ClienteFisico savedModel = repository.save(model);

        // Create endereços
        request.enderecos().forEach(enderecoRequest -> {
            enderecoService.createForCliente(savedModel.getId(), enderecoRequest);
        });

        log.info("Created ClienteFisico with ID: {} and {} endereços", savedModel.getId(), request.enderecos().size());
        return mapper.toResponse(savedModel);
    }

    @Override
    @Transactional
    public ClienteFisicoDTO.Response update(Long id, ClienteFisicoDTO.UpdateRequest request) {
        log.debug("Updating ClienteFisico with ID: {}", id);
        ClienteFisico model = findModelById(id);

        ensureClientIsActive(model);
        mapper.updateEntity(request, model);

        ClienteFisico updatedModel = repository.save(model);
        log.info("Updated ClienteFisico with ID: {}", id);
        return mapper.toResponse(updatedModel);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // Validate cliente has addresses before soft delete
        if (!enderecoService.hasAtLeastOneAddress(id)) {
            throw new BusinessException("Não é possível deletar um cliente sem endereços.");
        }

        // Soft delete
        this.inactivate(id);
    }

    @Override
    @Transactional
    public void activate(Long id) {
        log.debug("Activating ClienteFisico with ID: {}", id);
        ClienteFisico model = findModelById(id);

        if (Boolean.TRUE.equals(model.getEstaAtivo())) {
            throw new BusinessException("Este cliente já está ativo.");
        }

        model.setEstaAtivo(true);
        repository.save(model);
        log.info("Activated ClienteFisico with ID: {}", id);
    }

    @Override
    @Transactional
    public void inactivate(Long id) {
        log.debug("Inactivating ClienteFisico with ID: {}", id);
        ClienteFisico model = findModelById(id);

        if (Boolean.FALSE.equals(model.getEstaAtivo())) {
            throw new BusinessException("Este cliente já está inativo.");
        }

        model.setEstaAtivo(false);
        repository.save(model);
        log.info("Inactivated ClienteFisico with ID: {}", id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public ClienteFisico findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));
    }

    @Override
    @Transactional
    public void hardDelete(Long id) {
        log.debug("Hard deleting ClienteFisico with ID: {}", id);
        ClienteFisico model = findModelById(id);
        repository.delete(model);
        log.info("Hard deleted ClienteFisico with ID: {}", id);
    }

    @Override
    public ClienteFisicoDTO.Response findById(Long id) {
        return mapper.toResponse(findModelById(id));
    }

    @Override
    public Page<ClienteFisicoDTO.ListResponse> findAllActive(Pageable pageable) {
        return repository.findByEstaAtivoTrue(pageable)
                .map(mapper::toListResponse);
    }

    @Override
    public Page<ClienteFisicoDTO.ListResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toListResponse);
    }

    @Override
    public Page<ClienteFisicoDTO.ReportResponse> findAllForReport(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toReportResponse);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return repository.existsByCpf(cpf);
    }

    @Override
    public ClienteFisicoDTO.Response findByCpf(String cpf) {
        return repository.findByCpf(cpf)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o CPF: " + cpf));
    }

    // ==================== Private Helper Methods ====================

    private ClienteFisico findModelById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + id));
    }

    private void ensureClientIsActive(ClienteFisico model) {
        if (Boolean.FALSE.equals(model.getEstaAtivo())) {
            throw new BusinessException("Operação não permitida: O cliente está inativo.");
        }
    }

    private void validateCpfUniqueness(String cpf) {
        if (repository.existsByCpf(cpf)) {
            throw new BusinessException("Já existe um cliente cadastrado com este CPF.");
        }
    }

    private void validateAtLeastOneAddress(ClienteFisicoDTO.UpdateRequest request, Long clienteId) {
        if (request.enderecos() != null && request.enderecos().isEmpty()) {
            // Check if cliente has existing addresses
            if (!enderecoService.hasAtLeastOneAddress(clienteId)) {
                throw new BusinessException("Cliente deve ter pelo menos um endereço cadastrado.");
            }
        }

        if (request.enderecos() != null) {
            boolean hasPrincipal = request.enderecos().stream()
                    .anyMatch(endereco -> Boolean.TRUE.equals(endereco.principal()));

            if (!hasPrincipal && enderecoService.hasAtLeastOneAddress(clienteId)) {
                // Check if there's already a principal address
                if (!enderecoService.hasPrincipalAddress(clienteId)) {
                    throw new BusinessException("Cliente deve ter pelo menos um endereço marcado como principal.");
                }
            }
        }
    }
}