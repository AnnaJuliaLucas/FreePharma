package com.annaehugo.freepharma.domain.repository.fiscal;

import com.annaehugo.freepharma.domain.entity.fiscal.LoteProcessamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoteProcessamentoRepository extends JpaRepository<LoteProcessamento, Long> {
    List<LoteProcessamento> findByStatus(String status);
}
