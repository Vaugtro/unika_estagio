package com.desafio.estagio.repository;

import com.desafio.estagio.mvc.model.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ClienteRepository<T extends ClienteEntity> extends JpaRepository<T, Long> {

}
