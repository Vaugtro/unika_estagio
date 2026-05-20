package com.desafio.estagio.service.query;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoReportResponse;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Read-only query operations for ClienteFisico.
 */
public interface ClienteFisicoQueryService {

    ClienteFisicoResponse findById(Long id);

    ClienteFisicoListResponse findByIdList(Long id);

    Page<ClienteFisicoListResponse> findAllActive(Pageable pageable);

    Page<ClienteFisicoListResponse> findAll(Pageable pageable);

    Page<ClienteFisicoReportResponse> findAllForReport(Pageable pageable);

    boolean existsByCpf(String cpf);

    ClienteFisicoResponse findByCpf(String cpf);

    long count();
}
