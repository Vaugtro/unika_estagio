package com.desafio.estagio.repository;

import com.desafio.estagio.model.ClienteFisicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteFisicoRepository extends JpaRepository<ClienteFisicoEntity, Long>, ClienteRepository<ClienteFisicoEntity> {

    // Find by exact CPF (cleaned, 11 digits)
    Optional<ClienteFisicoEntity> findByCpf(String cpf);

    // Check if CPF exists
    boolean existsByCpf(String cpf);

    // Optional: Find by CPF ignoring case (though CPF is digits only)
    Optional<ClienteFisicoEntity> findByCpfIgnoreCase(String cpf);

    // Optional: Search with LIKE (for partial matches)
    @Query("SELECT c FROM ClienteFisicoEntity c WHERE c.cpf LIKE %:cpf%")
    List<ClienteFisicoEntity> searchByCpf(@Param("cpf") String cpf);

    // Optional: Find by CPF and active status
    Optional<ClienteFisicoEntity> findByCpfAndEstaAtivoTrue(String cpf);

    boolean existsByRg(String cleanedRg);
}