package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.fiscal.HistoricoNotaFiscal;
import com.annaehugo.freepharma.domain.repository.fiscal.HistoricoNotaFiscalRepository;
import com.annaehugo.freepharma.domain.repository.fiscal.NotaFiscalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HistoricoNotaFiscalService {

    private final HistoricoNotaFiscalRepository historicoNotaFiscalRepository;
    private final NotaFiscalRepository notaFiscalRepository;

    @Autowired
    public HistoricoNotaFiscalService(
            HistoricoNotaFiscalRepository historicoNotaFiscalRepository,
            NotaFiscalRepository notaFiscalRepository) {
        this.historicoNotaFiscalRepository = historicoNotaFiscalRepository;
        this.notaFiscalRepository = notaFiscalRepository;
    }

    public List<HistoricoNotaFiscal> listarTodos() {
        return historicoNotaFiscalRepository.findAll();
    }

    public List<HistoricoNotaFiscal> listarPorNotaFiscal(Long notaFiscalId) {
        return historicoNotaFiscalRepository.findByNotaFiscalId(notaFiscalId);
    }

    public List<HistoricoNotaFiscal> listarPorTipoOperacao(String tipoOperacao) {
        return historicoNotaFiscalRepository.findByTipoOperacao(tipoOperacao);
    }

    public Optional<HistoricoNotaFiscal> buscarPorId(Long id) {
        return historicoNotaFiscalRepository.findById(id);
    }

    public HistoricoNotaFiscal salvar(HistoricoNotaFiscal historicoNotaFiscal) {
        validarDadosHistorico(historicoNotaFiscal);
        
        if (!notaFiscalRepository.existsById(historicoNotaFiscal.getNotaFiscal().getId())) {
            throw new RuntimeException("Nota fiscal não encontrada");
        }
        
        historicoNotaFiscal.setDataOperacao(new Date());
        return historicoNotaFiscalRepository.save(historicoNotaFiscal);
    }

    public HistoricoNotaFiscal atualizar(Long id, HistoricoNotaFiscal historicoAtualizado) {
        return historicoNotaFiscalRepository.findById(id)
                .map(historico -> {
                    historicoAtualizado.setId(id);
                    validarDadosHistorico(historicoAtualizado);
                    return historicoNotaFiscalRepository.save(historicoAtualizado);
                })
                .orElseThrow(() -> new RuntimeException("Histórico não encontrado"));
    }

    public void deletar(Long id) {
        if (!historicoNotaFiscalRepository.existsById(id)) {
            throw new RuntimeException("Histórico não encontrado");
        }
        historicoNotaFiscalRepository.deleteById(id);
    }

    private void validarDadosHistorico(HistoricoNotaFiscal historico) {
        if (historico.getNotaFiscal() == null || historico.getNotaFiscal().getId() == null) {
            throw new RuntimeException("Nota fiscal é obrigatória");
        }
        
        if (historico.getTipoOperacao() == null || historico.getTipoOperacao().trim().isEmpty()) {
            throw new RuntimeException("Tipo de operação é obrigatório");
        }
        
        if (historico.getStatusAnterior() == null || historico.getStatusAnterior().trim().isEmpty()) {
            throw new RuntimeException("Status anterior é obrigatório");
        }
        
        if (historico.getStatusNovo() == null || historico.getStatusNovo().trim().isEmpty()) {
            throw new RuntimeException("Status novo é obrigatório");
        }
    }
}