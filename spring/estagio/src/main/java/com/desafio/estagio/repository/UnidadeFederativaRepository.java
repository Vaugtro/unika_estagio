package com.desafio.estagio.repository;

import com.desafio.estagio.model.UnidadeFederativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnidadeFederativaRepository extends JpaRepository<UnidadeFederativa, Long> {

    Optional<UnidadeFederativa> findBySigla(String sigla);

    List<UnidadeFederativa> findAllByOrderByNome();
}
