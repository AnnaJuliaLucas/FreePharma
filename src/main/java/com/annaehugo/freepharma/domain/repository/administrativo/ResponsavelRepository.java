package com.annaehugo.freepharma.domain.repository.administrativo;

import com.annaehugo.freepharma.domain.entity.administrativo.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {
    Optional<Responsavel> findByEmail(String email);
    Optional<Responsavel> findByCpfCnpj(String cpfCnpj);
    
    List<Responsavel> findByFarmaciaId(Long farmaciaId);
    List<Responsavel> findByAtivoTrue();

    boolean existsByEmail(String email);
    boolean existsByCpfCnpj(String cpfCnpj);
}
