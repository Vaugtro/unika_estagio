package com.desafio.estagio.service;

import com.desafio.estagio.service.lifecycle.ClienteJuridicoLifecycleService;
import com.desafio.estagio.service.query.ClienteJuridicoQueryService;

/**
 * Service interface for ClienteJuridico (Business Client).
 * Standardized with ClienteFisicoService for project consistency.
 */
public interface ClienteJuridicoService extends ClienteJuridicoQueryService, ClienteJuridicoLifecycleService {
}