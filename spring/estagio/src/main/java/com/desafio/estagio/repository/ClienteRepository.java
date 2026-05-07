package com.desafio.estagio.repository;

import com.desafio.estagio.mvc.model.entity.Cliente;
import com.desafio.estagio.mvc.model.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository<T extends ClienteEntity> extends JpaRepository<T, Long> {
}
