package com.annaehugo.freepharma.domain.repository.fiscal;

import com.annaehugo.freepharma.domain.entity.fiscal.Inconsistencia;
import com.annaehugo.freepharma.domain.entity.fiscal.TipoInconsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InconsistenciaRepository extends JpaRepository<Inconsistencia, Long> {
    List<Inconsistencia> findByNotaFiscalId(Long notaFiscalId);
    List<Inconsistencia> findByTipo(TipoInconsistencia tipo);
    List<Inconsistencia> findByStatus(String status);
    List<Inconsistencia> findByStatusNot(String status);
}
