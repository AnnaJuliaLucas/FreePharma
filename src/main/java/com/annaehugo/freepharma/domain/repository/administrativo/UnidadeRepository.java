package com.annaehugo.freepharma.domain.repository.administrativo;

import com.annaehugo.freepharma.domain.entity.administrativo.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnidadeRepository extends JpaRepository<Unidade, Long> {
    List<Unidade> findByFarmaciaId(Long farmaciaId);

    Optional<Unidade> findByCnpj(String cnpj);

    List<Unidade> findByStatus(String status);
}
