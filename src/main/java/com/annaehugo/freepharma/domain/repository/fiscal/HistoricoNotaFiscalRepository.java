package com.annaehugo.freepharma.domain.repository.fiscal;

import com.annaehugo.freepharma.domain.entity.fiscal.HistoricoNotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoNotaFiscalRepository extends JpaRepository<HistoricoNotaFiscal, Long> {
    List<HistoricoNotaFiscal> findByNotaFiscalId(Long notaFiscalId);
    List<HistoricoNotaFiscal> findByTipoOperacao(String tipoOperacao);
}
