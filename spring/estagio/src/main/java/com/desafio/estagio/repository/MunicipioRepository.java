package com.desafio.estagio.repository;

import com.desafio.estagio.model.Municipio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Long> {

    List<Municipio> findByUnidadeFederativaSiglaOrderByNome(String sigla);

    List<Municipio> findAllByOrderByNome();

    Optional<Municipio> findByNomeAndUnidadeFederativaSigla(String nome, String sigla);

    @Query("SELECT m FROM Municipio m WHERE LOWER(REPLACE(m.nome, ' ', '')) LIKE LOWER(CONCAT('%', REPLACE(:nome, ' ', ''), '%')) AND m.unidadeFederativa.sigla = :sigla")
    List<Municipio> fuzzyFindByNomeAndSigla(@Param("nome") String nome, @Param("sigla") String sigla);
}
