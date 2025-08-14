package com.annaehugo.freepharma.domain.repository.administrativo;

import com.annaehugo.freepharma.domain.entity.administrativo.Farmacia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmaciaRepository extends JpaRepository<Farmacia, Long> {
    Optional<Farmacia> findByNomeFantasia(String nome);
    Optional<Farmacia> findByCnpj(String cnpj);

    List<Farmacia> findByStatus(String status);
}
