package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.fiscal.LoteProcessamento;
import com.annaehugo.freepharma.domain.repository.fiscal.LoteProcessamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LoteProcessamentoService {

    private final LoteProcessamentoRepository loteProcessamentoRepository;

    @Autowired
    public LoteProcessamentoService(LoteProcessamentoRepository loteProcessamentoRepository) {
        this.loteProcessamentoRepository = loteProcessamentoRepository;
    }

    public List<LoteProcessamento> listarTodos() {
        return loteProcessamentoRepository.findAll();
    }

    public List<LoteProcessamento> listarPorStatus(String status) {
        return loteProcessamentoRepository.findByStatus(status);
    }

    public Optional<LoteProcessamento> buscarPorId(Long id) {
        return loteProcessamentoRepository.findById(id);
    }

    public LoteProcessamento salvar(LoteProcessamento loteProcessamento) {
        if (loteProcessamento.getDataInicio() == null) {
            loteProcessamento.setDataInicio(new Date());
        }
        if (loteProcessamento.getStatus() == null) {
            loteProcessamento.setStatus("INICIADO");
        }
        return loteProcessamentoRepository.save(loteProcessamento);
    }

    public LoteProcessamento finalizar(Long id, String status) {
        return loteProcessamentoRepository.findById(id)
                .map(lote -> {
                    lote.setStatus(status);
                    lote.setDataFim(new Date());
                    return loteProcessamentoRepository.save(lote);
                })
                .orElseThrow(() -> new RuntimeException("Lote não encontrado"));
    }
}