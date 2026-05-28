package com.desafio.estagio.service;

import com.desafio.estagio.model.Municipio;
import com.desafio.estagio.model.UnidadeFederativa;
import com.desafio.estagio.repository.MunicipioRepository;
import com.desafio.estagio.repository.UnidadeFederativaRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class IbgeService {

    private static final Logger log = LoggerFactory.getLogger(IbgeService.class);
    private static final String IBGE_MUNICIPIOS_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/municipios";

    private final MunicipioRepository municipioRepository;
    private final UnidadeFederativaRepository unidadeFederativaRepository;
    private final RestTemplate restTemplate;

    public IbgeService(MunicipioRepository municipioRepository,
                       UnidadeFederativaRepository unidadeFederativaRepository) {
        this.municipioRepository = municipioRepository;
        this.unidadeFederativaRepository = unidadeFederativaRepository;
        this.restTemplate = new RestTemplate();
    }

    @PostConstruct
    @Transactional
    public void init() {
        if (municipioRepository.count() > 0) {
            log.info("Municipio table already populated ({} records), skipping IBGE fetch", municipioRepository.count());
            return;
        }
        fetchAndSaveMunicipios();
    }

    @SuppressWarnings("unchecked")
    public void fetchAndSaveMunicipios() {
        log.info("Fetching municipios from IBGE API...");
        try {
            var response = restTemplate.getForObject(IBGE_MUNICIPIOS_URL, List.class);
            if (response == null) {
                log.warn("IBGE API returned null response");
                return;
            }
            var municipios = response.stream()
                    .map(raw -> toMunicipio((Map<String, Object>) raw))
                    .filter(m -> m != null)
                    .toList();
            municipioRepository.saveAll(municipios);
            log.info("Saved {} municipios from IBGE API", municipios.size());
        } catch (Exception e) {
            log.error("Failed to fetch municipios from IBGE API: {}", e.getMessage());
        }
    }

    private Municipio toMunicipio(Map<String, Object> data) {
        try {
            Object idRaw = data.get("id");
            if (idRaw == null) return null;
            Long id = ((Number) idRaw).longValue();
            String nome = (String) data.get("nome");

            Map<String, Object> microrregiao = (Map<String, Object>) data.get("microrregiao");
            if (microrregiao == null) return null;
            Map<String, Object> mesorregiao = (Map<String, Object>) microrregiao.get("mesorregiao");
            if (mesorregiao == null) return null;
            Map<String, Object> ufData = (Map<String, Object>) mesorregiao.get("UF");
            if (ufData == null) return null;
            String sigla = (String) ufData.get("sigla");
            if (sigla == null) return null;

            UnidadeFederativa uf = unidadeFederativaRepository.findBySigla(sigla).orElse(null);
            if (uf == null) {
                log.warn("UF not found for sigla: {}", sigla);
                return null;
            }

            return new Municipio(id, nome, uf);
        } catch (Exception e) {
            log.warn("Failed to parse municipio from IBGE data: {}", e.getMessage());
            return null;
        }
    }
}
