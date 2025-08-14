package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.fiscal.Inconsistencia;
import com.annaehugo.freepharma.domain.entity.fiscal.TipoInconsistencia;
import com.annaehugo.freepharma.domain.repository.fiscal.InconsistenciaRepository;
import com.annaehugo.freepharma.domain.repository.fiscal.NotaFiscalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InconsistenciaService {

    private final InconsistenciaRepository inconsistenciaRepository;
    private final NotaFiscalRepository notaFiscalRepository;

    @Autowired
    public InconsistenciaService(
            InconsistenciaRepository inconsistenciaRepository,
            NotaFiscalRepository notaFiscalRepository) {
        this.inconsistenciaRepository = inconsistenciaRepository;
        this.notaFiscalRepository = notaFiscalRepository;
    }

    public List<Inconsistencia> listarTodas() {
        return inconsistenciaRepository.findAll();
    }

    public List<Inconsistencia> listarPorNotaFiscal(Long notaFiscalId) {
        return inconsistenciaRepository.findByNotaFiscalId(notaFiscalId);
    }


    public List<Inconsistencia> listarPorTipo(TipoInconsistencia tipo) {
        return inconsistenciaRepository.findByTipo(tipo);
    }

    public List<Inconsistencia> listarPorStatus(String status) {
        return inconsistenciaRepository.findByStatus(status);
    }

    public List<Inconsistencia> listarNaoResolvidas() {
        return inconsistenciaRepository.findByStatusNot("RESOLVIDA");
    }

    public Optional<Inconsistencia> buscarPorId(Long id) {
        return inconsistenciaRepository.findById(id);
    }

    public Inconsistencia salvar(Inconsistencia inconsistencia) {
        validarDadosInconsistencia(inconsistencia);
        
        if (!notaFiscalRepository.existsById(inconsistencia.getNotaFiscal().getId())) {
            throw new RuntimeException("Nota fiscal não encontrada");
        }
        
        inconsistencia.setDataDeteccao(new Date());
        if (inconsistencia.getStatus() == null || inconsistencia.getStatus().trim().isEmpty()) {
            inconsistencia.setStatus("PENDENTE");
        }
        
        return inconsistenciaRepository.save(inconsistencia);
    }

    public Inconsistencia atualizar(Long id, Inconsistencia inconsistenciaAtualizada) {
        return inconsistenciaRepository.findById(id)
                .map(inconsistencia -> {
                    inconsistenciaAtualizada.setId(id);
                    validarDadosInconsistencia(inconsistenciaAtualizada);
                    
                    // Manter data de detecção original
                    inconsistenciaAtualizada.setDataDeteccao(inconsistencia.getDataDeteccao());
                    
                    return inconsistenciaRepository.save(inconsistenciaAtualizada);
                })
                .orElseThrow(() -> new RuntimeException("Inconsistência não encontrada"));
    }

    public void deletar(Long id) {
        if (!inconsistenciaRepository.existsById(id)) {
            throw new RuntimeException("Inconsistência não encontrada");
        }
        inconsistenciaRepository.deleteById(id);
    }

    public Inconsistencia resolver(Long id, String observacaoResolucao) {
        return inconsistenciaRepository.findById(id)
                .map(inconsistencia -> {
                    inconsistencia.setStatus("RESOLVIDA");
                    inconsistencia.setDataResolucao(new Date());
                    inconsistencia.setObservacaoResolucao(observacaoResolucao);
                    return inconsistenciaRepository.save(inconsistencia);
                })
                .orElseThrow(() -> new RuntimeException("Inconsistência não encontrada"));
    }

    public Inconsistencia reabrir(Long id, String motivo) {
        return inconsistenciaRepository.findById(id)
                .map(inconsistencia -> {
                    inconsistencia.setStatus("REABERTA");
                    inconsistencia.setDataResolucao(null);
                    inconsistencia.setObservacaoResolucao(motivo);
                    return inconsistenciaRepository.save(inconsistencia);
                })
                .orElseThrow(() -> new RuntimeException("Inconsistência não encontrada"));
    }

    private void validarDadosInconsistencia(Inconsistencia inconsistencia) {
        if (inconsistencia.getNotaFiscal() == null || inconsistencia.getNotaFiscal().getId() == null) {
            throw new RuntimeException("Nota fiscal é obrigatória");
        }
        
        if (inconsistencia.getTipo() == null || inconsistencia.getTipo().getDescricao().trim().isEmpty()) {
            throw new RuntimeException("Tipo de inconsistência é obrigatório");
        }
        
        if (inconsistencia.getDescricao() == null || inconsistencia.getDescricao().trim().isEmpty()) {
            throw new RuntimeException("Descrição é obrigatória");
        }
        
        if (inconsistencia.getSeveridade() == null || inconsistencia.getSeveridade().trim().isEmpty()) {
            throw new RuntimeException("Severidade é obrigatória");
        }

        String[] tiposValidos = {"VALOR_DIVERGENTE", "PRODUTO_INEXISTENTE", "CNPJ_INVALIDO", "DATA_INVALIDA", "ESTRUTURA_XML"};
        boolean tipoValido = false;
        for (String tipo : tiposValidos) {
            if (tipo.equals(inconsistencia.getTipo())) {
                tipoValido = true;
                break;
            }
        }
        if (!tipoValido) {
            throw new RuntimeException("Tipo de inconsistência inválido");
        }

        String[] severidadesValidas = {"BAIXA", "MEDIA", "ALTA", "CRITICA"};
        boolean severidadeValida = false;
        for (String severidade : severidadesValidas) {
            if (severidade.equals(inconsistencia.getSeveridade())) {
                severidadeValida = true;
                break;
            }
        }
        if (!severidadeValida) {
            throw new RuntimeException("Severidade inválida");
        }
    }
}