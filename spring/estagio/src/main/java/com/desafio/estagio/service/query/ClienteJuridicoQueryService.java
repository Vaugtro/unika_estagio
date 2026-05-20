package com.desafio.estagio.service.query;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoListResponse;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoReportResponse;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Read-only query operations for ClienteJuridico.
 */
public interface ClienteJuridicoQueryService {

    ClienteJuridicoResponse findById(Long id);

    ClienteJuridicoListResponse findByIdList(Long id);

    Page<ClienteJuridicoListResponse> findAll(Pageable pageable);

    List<ClienteJuridicoResponse> findAll();

    Page<ClienteJuridicoListResponse> findAllActive(Pageable pageable);

    Page<ClienteJuridicoReportResponse> findAllForReport(Pageable pageable);

    boolean existsByCnpj(String cnpj);

    ClienteJuridicoResponse findByCnpj(String cnpj);

    long count();
}
