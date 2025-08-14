package com.annaehugo.freepharma.domain.repository.fiscal;

import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, Long> {
    List<NotaFiscal> findByUnidadeId(Long id);
    List<NotaFiscal> findByStatus(String status);
    List<NotaFiscal> findByTipoOperacao(String tipoOperacao);
    List<NotaFiscal> findByDataEmissaoBetween(Date dataEmissaoAfter, Date dataEmissaoBefore);
    List<NotaFiscal> findByFornecedorId(Long fornecedorId);
    List<NotaFiscal> findByClienteId(Long clienteId);

    Optional<NotaFiscal> findByChaveAcesso(String chaveAcesso);
    Optional<NotaFiscal> findByNumero(String numero);
    Long countByDataEmissaoBetween(Date dataEmissaoAfter, Date dataEmissaoBefore);
}
