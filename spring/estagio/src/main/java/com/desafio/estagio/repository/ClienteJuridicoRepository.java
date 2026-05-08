package com.desafio.estagio.repository;

import com.desafio.estagio.model.ClienteJuridicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteJuridicoRepository extends JpaRepository<ClienteJuridicoEntity, Long>, ClienteRepository<ClienteJuridicoEntity> {

    // Find by CNPJ (exact match)
    Optional<ClienteJuridicoEntity> findByCnpj(String cnpj);

    // Check if CNPJ exists
    boolean existsByCnpj(String cnpj);

    // Find by active status
    List<ClienteJuridicoEntity> findByEstaAtivoTrue();

    List<ClienteJuridicoEntity> findByEstaAtivoFalse();

    // Find by CNPJ containing (for search functionality)
    List<ClienteJuridicoEntity> findByCnpjContaining(String cnpj);

    // Find by Razão Social (partial match)
    List<ClienteJuridicoEntity> findByRazaoSocialContainingIgnoreCase(String razaoSocial);

    // Find by Inscrição Estadual
    Optional<ClienteJuridicoEntity> findByInscricaoEstadual(String inscricaoEstadual);
}