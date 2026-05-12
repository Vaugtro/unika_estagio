package com.desafio.estagio.repository;


import com.desafio.estagio.model.Endereco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    Page<Endereco> findByClienteId(Long clienteId, Pageable pageable);

    List<Endereco> findByClienteId(Long clienteId);

    Optional<Endereco> findByClienteIdAndPrincipalTrue(Long clienteId);

    long countByClienteId(Long clienteId);

    boolean existsByClienteIdAndPrincipalTrue(Long clienteId);

    long deleteByClienteId(Long clienteId);
}