package com.desafio.estagio.service;

import com.desafio.estagio.service.lifecycle.ClienteFisicoLifecycleService;
import com.desafio.estagio.service.query.ClienteFisicoQueryService;

/**
 * Service interface for ClienteFisico (Individual Client).
 * Defines business operations using modern Spring best practices:
 * - Pagination for all list operations.
 * - RuntimeExceptions (no explicit throws clauses).
 */
public interface ClienteFisicoService extends ClienteFisicoQueryService, ClienteFisicoLifecycleService {
}