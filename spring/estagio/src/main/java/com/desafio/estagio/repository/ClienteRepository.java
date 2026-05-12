package com.desafio.estagio.repository;

import com.desafio.estagio.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository<T extends Cliente> extends JpaRepository<T, Long> {
    long countByEstaAtivoTrue();
    long countByEstaAtivoFalse();
    Page<T> findByEstaAtivoTrue(Pageable pageable);  // Fixed
    Page<T> findByEstaAtivoFalse(Pageable pageable); // Also useful
}
