package com.desafio.estagio.repository;

import com.desafio.estagio.model.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository<T extends ClienteEntity> extends JpaRepository<T, Long> {
}
