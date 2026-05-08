package com.desafio.estagio.repository;

import com.desafio.estagio.model.EnderecoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoRepository<T extends EnderecoEntity> extends JpaRepository<T, Long> {

    Optional<EnderecoEntity> findByClienteIdAndPrincipalTrue(Long clienteId);

    List<EnderecoEntity> findByClienteId(Long clienteId);

    long countByClienteId(Long clienteId);
}
