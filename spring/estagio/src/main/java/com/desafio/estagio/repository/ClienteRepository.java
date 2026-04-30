package com.desafio.estagio.repository;

import com.desafio.estagio.mvc.model.entity.ICliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ClienteRepository<T extends ICliente> extends JpaRepository<T, Long> {
}
