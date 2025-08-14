package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscal;
import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscalItem;
import com.annaehugo.freepharma.domain.entity.fiscal.Inconsistencia;
import com.annaehugo.freepharma.domain.repository.fiscal.NotaFiscalRepository;
import com.annaehugo.freepharma.domain.repository.fiscal.NotaFiscalItemRepository;
import com.annaehugo.freepharma.domain.repository.fiscal.InconsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Date;

@Service
@Transactional(readOnly = true)
public class NotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final NotaFiscalItemRepository notaFiscalItemRepository;
    private final InconsistenciaRepository inconsistenciaRepository;

    @Autowired
    public NotaFiscalService(
            NotaFiscalRepository notaFiscalRepository,
            NotaFiscalItemRepository notaFiscalItemRepository,
            InconsistenciaRepository inconsistenciaRepository) {
        this.notaFiscalRepository = notaFiscalRepository;
        this.notaFiscalItemRepository = notaFiscalItemRepository;
        this.inconsistenciaRepository = inconsistenciaRepository;
    }

    public List<NotaFiscal> listarTodas() {
        return notaFiscalRepository.findAll();
    }

    public Page<NotaFiscal> listarComPaginacao(Pageable pageable) {
        return notaFiscalRepository.findAll(pageable);
    }

    public List<NotaFiscal> listarPorUnidade(Long unidadeId) {
        return notaFiscalRepository.findByUnidadeId(unidadeId);
    }

    public List<NotaFiscal> listarPorStatus(String status) {
        return notaFiscalRepository.findByStatus(status);
    }

    public List<NotaFiscal> listarPorTipoOperacao(String tipoOperacao) {
        return notaFiscalRepository.findByTipoOperacao(tipoOperacao);
    }

    public List<NotaFiscal> listarPorPeriodo(Date dataInicio, Date dataFim) {
        return notaFiscalRepository.findByDataEmissaoBetween(dataInicio, dataFim);
    }

    public Optional<NotaFiscal> buscarPorId(Long id) {
        return notaFiscalRepository.findById(id);
    }

    public Optional<NotaFiscal> buscarPorChaveAcesso(String chaveAcesso) {
        return notaFiscalRepository.findByChaveAcesso(chaveAcesso);
    }

    public Optional<NotaFiscal> buscarPorNumero(String numero) {
        return notaFiscalRepository.findByNumero(numero);
    }

    public List<NotaFiscalItem> listarItensPorNota(Long notaFiscalId) {
        return notaFiscalItemRepository.findByNotaFiscalId(notaFiscalId);
    }

    public List<Inconsistencia> listarInconsistenciasPorNota(Long notaFiscalId) {
        return inconsistenciaRepository.findByNotaFiscalId(notaFiscalId);
    }


    public List<NotaFiscal> buscarPorFornecedor(Long fornecedorId) {
        return notaFiscalRepository.findByFornecedorId(fornecedorId);
    }

    public List<NotaFiscal> buscarPorCliente(Long clienteId) {
        return notaFiscalRepository.findByClienteId(clienteId);
    }

    public Long contarNotasPorPeriodo(Date dataInicio, Date dataFim) {
        return notaFiscalRepository.countByDataEmissaoBetween(dataInicio, dataFim);
    }
}