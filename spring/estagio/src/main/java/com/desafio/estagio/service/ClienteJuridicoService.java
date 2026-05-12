package com.desafio.estagio.service;

import com.desafio.estagio.dto.ClienteJuridicoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for ClienteJuridico (Business Client).
 * Standardized with ClienteFisicoService for project consistency.
 */
public interface ClienteJuridicoService {

    ClienteJuridicoDTO.Response create(ClienteJuridicoDTO.CreateRequest request);

    ClienteJuridicoDTO.Response update(Long id, ClienteJuridicoDTO.UpdateRequest request);

    // Standardized name (removed DTO suffix)
    ClienteJuridicoDTO.Response findById(Long id);

    // Standardized name (consistent with Fisico)
    Page<ClienteJuridicoDTO.ListResponse> findAll(Pageable pageable);
    List<ClienteJuridicoDTO.Response> findAll();

    // Standardized name (consistent with Fisico)
    Page<ClienteJuridicoDTO.ListResponse> findAllActive(Pageable pageable);

    Page<ClienteJuridicoDTO.ReportResponse> findAllForReport(Pageable pageable);

    boolean existsByCnpj(String cnpj);

    ClienteJuridicoDTO.Response findByCnpj(String cnpj);

    // Renamed to match the "soft delete" logic
    void delete(Long id);

    void hardDelete(Long id);

    void activate(Long id);

    void inactivate(Long id);

}