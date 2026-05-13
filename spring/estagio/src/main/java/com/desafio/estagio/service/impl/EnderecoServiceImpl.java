package com.desafio.estagio.service.impl;

import com.desafio.estagio.dto.endereco.*;
import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.exceptions.ResourceNotFoundException;
import com.desafio.estagio.mapper.EnderecoMapper;
import com.desafio.estagio.model.Cliente;
import com.desafio.estagio.model.Endereco;
import com.desafio.estagio.repository.ClienteRepository;
import com.desafio.estagio.repository.EnderecoRepository;
import com.desafio.estagio.service.EnderecoService;
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
public class EnderecoServiceImpl implements EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository<Cliente> clienteRepository;
    private final EnderecoMapper enderecoMapper;

    // =====================================================
    // CREATE OPERATIONS
    // =====================================================

    @Override
    @Transactional
    public EnderecoResponse create(EnderecoCreateRequest request) {
        log.debug("Creating endereco");

        if (request.clienteId() == null) {
            throw new BusinessException("Cliente ID é obrigatório para criar um endereço.");
        }

        Endereco entity = enderecoMapper.toEntity(request);
        Cliente cliente = findClienteById(request.clienteId());
        entity.setCliente(cliente);

        handlePrincipalLogic(entity, cliente.getId());

        Endereco saved = enderecoRepository.save(entity);
        log.info("Created endereco with ID: {}", saved.getId());

        return enderecoMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public EnderecoResponse createForCliente(Long clienteId, EnderecoWithinClienteCreateRequest request) {
        log.debug("Creating endereco for cliente ID: {}", clienteId);

        Cliente cliente = findClienteById(clienteId);
        Endereco entity = enderecoMapper.toEntity(request);
        entity.setCliente(cliente);

        handlePrincipalLogic(entity, clienteId);

        Endereco saved = enderecoRepository.save(entity);
        log.info("Created endereco with ID: {} for cliente ID: {}", saved.getId(), clienteId);

        return enderecoMapper.toResponse(saved);
    }

    // =====================================================
    // READ OPERATIONS
    // =====================================================

    @Override
    public EnderecoResponse findById(Long id) {
        log.debug("Finding endereco by ID: {}", id);
        Endereco entity = findEntityById(id);
        return enderecoMapper.toResponse(entity);
    }

    @Override
    public Page<EnderecoListResponse> findAllByClienteId(Long clienteId, Pageable pageable) {
        log.debug("Finding all enderecos for cliente ID: {} with pagination", clienteId);

        // Verify cliente exists
        findClienteById(clienteId);

        return enderecoRepository.findByClienteId(clienteId, pageable)
                .map(enderecoMapper::toListResponse);
    }

    @Override
    public List<EnderecoResponse> findAllByClienteId(Long clienteId) {
        log.debug("Finding all enderecos for cliente ID: {}", clienteId);

        // Verify cliente exists
        findClienteById(clienteId);

        return enderecoRepository.findByClienteId(clienteId).stream()
                .map(enderecoMapper::toResponse)
                .toList();
    }

    @Override
    public EnderecoResponse findPrincipalByClienteId(Long clienteId) {
        log.debug("Finding principal endereco for cliente ID: {}", clienteId);

        findClienteById(clienteId);

        Endereco entity = enderecoRepository.findByClienteIdAndPrincipalTrue(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Endereço principal não encontrado para o cliente ID: %d", clienteId)
                ));

        return enderecoMapper.toResponse(entity);
    }

    @Override
    public long countByClienteId(Long clienteId) {
        return enderecoRepository.countByClienteId(clienteId);
    }

    // =====================================================
    // UPDATE OPERATIONS
    // =====================================================

    @Override
    @Transactional
    public EnderecoResponse update(Long id, EnderecoUpdateRequest request) {
        log.debug("Updating endereco with ID: {}", id);

        Endereco existing = findEntityById(id);
        Long clienteId = existing.getCliente().getId();

        // Update fields
        enderecoMapper.updateEntity(request, existing);

        // Handle principal flag — delegate to model
        if (Boolean.TRUE.equals(request.principal())) {
            Cliente cliente = existing.getCliente();
            cliente.setEnderecoPrincipal(existing);
        } else if (Boolean.FALSE.equals(request.principal()) && existing.getPrincipal()) {
            // Prevent removing principal if it's the only address
            long addressCount = enderecoRepository.countByClienteId(clienteId);
            if (addressCount <= 1) {
                throw new BusinessException("Não é possível remover o endereço principal quando é o único endereço do cliente.");
            }
            existing.setPrincipal(false);
        }

        Endereco updated = enderecoRepository.save(existing);
        log.info("Updated endereco with ID: {}", id);

        return enderecoMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public EnderecoResponse setAsPrincipal(Long id) {
        log.debug("Setting endereco as principal: {}", id);

        Endereco entity = findEntityById(id);
        Cliente cliente = entity.getCliente();

        // Delegate to model — handles uniqueness (demotes current principal)
        cliente.setEnderecoPrincipal(entity);

        Endereco updated = enderecoRepository.save(entity);
        log.info("Set endereco {} as principal for cliente {}", id, cliente.getId());

        return enderecoMapper.toResponse(updated);
    }

    // =====================================================
    // DELETE OPERATIONS
    // =====================================================

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting endereco with ID: {}", id);

        Endereco entity = findEntityById(id);
        Cliente cliente = entity.getCliente();

        // Delegate to model — enforces at-least-one and principal promotion rules
        try {
            cliente.removeEndereco(entity);
        } catch (IllegalStateException e) {
            throw new BusinessException(e.getMessage());
        }

        enderecoRepository.delete(entity);
        log.info("Deleted endereco with ID: {}", id);
    }

    @Override
    @Transactional
    public void deleteAllByClienteId(Long clienteId) {
        log.debug("Deleting all enderecos for cliente ID: {}", clienteId);

        // Verify cliente exists
        findClienteById(clienteId);

        long deletedCount = enderecoRepository.deleteByClienteId(clienteId);
        log.info("Deleted {} enderecos for cliente ID: {}", deletedCount, clienteId);
    }

    // =====================================================
    // VALIDATION OPERATIONS
    // =====================================================

    @Override
    public boolean hasAtLeastOneAddress(Long clienteId) {
        return countByClienteId(clienteId) > 0;
    }

    @Override
    public boolean hasPrincipalAddress(Long clienteId) {
        return enderecoRepository.existsByClienteIdAndPrincipalTrue(clienteId);
    }

    // =====================================================
    // PRIVATE HELPER METHODS
    // =====================================================

    private Endereco findEntityById(Long id) {
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Endereço não encontrado com o ID: %d", id)
                ));
    }

    private Cliente findClienteById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Cliente não encontrado com o ID: %d", id)
                ));
    }

    /**
     * Delegates principal logic to the model's addEndereco method.
     * The model ensures first address is principal and handles principal uniqueness.
     */
    private void handlePrincipalLogic(Endereco entity, Long clienteId) {
        // The model's addEndereco() already handles:
        // - First address must be principal
        // - If new address is principal, demotes others
        // So we only need to handle the case where this is the first address
        long addressCount = enderecoRepository.countByClienteId(clienteId);
        if (addressCount == 0) {
            entity.setPrincipal(true);
            log.debug("First address for cliente {}, setting as principal", clienteId);
        } else if (entity.getPrincipal()) {
            // Demote existing principal via model
            Cliente cliente = findClienteById(clienteId);
            cliente.getEnderecos().forEach(e -> e.setPrincipal(false));
        }
    }
}