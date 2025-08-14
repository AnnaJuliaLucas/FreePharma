package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.estoque.HistoricoValorProduto;
import com.annaehugo.freepharma.domain.repository.estoque.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HistoricoValorProdutoService {

    private final HistoricoValorProdutoRepository historicoValorProdutoRepository;
    private final ProdutoReferenciaRepository produtoReferenciaRepository;

    @Autowired
    public HistoricoValorProdutoService(
            HistoricoValorProdutoRepository historicoValorProdutoRepository,
            ProdutoReferenciaRepository produtoReferenciaRepository) {
        this.historicoValorProdutoRepository = historicoValorProdutoRepository;
        this.produtoReferenciaRepository = produtoReferenciaRepository;
    }

    public List<HistoricoValorProduto> listarTodos() {
        return historicoValorProdutoRepository.findAll();
    }

//    public List<HistoricoValorProduto> listarPorProduto(Long produtoId) {
//        return historicoValorProdutoRepository.findAll(); // Implementação temporária
//    }

    public Optional<HistoricoValorProduto> buscarPorId(Long id) {
        return historicoValorProdutoRepository.findById(id);
    }

    public HistoricoValorProduto salvar(HistoricoValorProduto historicoValorProduto) {
        validarDadosHistorico(historicoValorProduto);
        
        if (historicoValorProduto.getEstoqueProduto() == null || historicoValorProduto.getEstoqueProduto().getId() == null) {
            throw new RuntimeException("Estoque produto não encontrado");
        }
        
        historicoValorProduto.setDataAlteracao(new Date());
        return historicoValorProdutoRepository.save(historicoValorProduto);
    }

    public HistoricoValorProduto atualizar(Long id, HistoricoValorProduto historicoAtualizado) {
        return historicoValorProdutoRepository.findById(id)
                .map(historico -> {
                    historicoAtualizado.setId(id);
                    validarDadosHistorico(historicoAtualizado);
                    historicoAtualizado.setDataAlteracao(new Date());
                    return historicoValorProdutoRepository.save(historicoAtualizado);
                })
                .orElseThrow(() -> new RuntimeException("Histórico não encontrado"));
    }

    public void deletar(Long id) {
        if (!historicoValorProdutoRepository.existsById(id)) {
            throw new RuntimeException("Histórico não encontrado");
        }
        historicoValorProdutoRepository.deleteById(id);
    }

    private void validarDadosHistorico(HistoricoValorProduto historico) {
        if (historico.getEstoqueProduto() == null || historico.getEstoqueProduto().getId() == null) {
            throw new RuntimeException("Estoque produto é obrigatório");
        }
        
        if (historico.getValorAnterior() == null) {
            throw new RuntimeException("Valor anterior é obrigatório");
        }
        
        if (historico.getValorNovo() == null) {
            throw new RuntimeException("Valor novo é obrigatório");
        }
        
        if (historico.getValorAnterior().equals(historico.getValorNovo())) {
            throw new RuntimeException("Valor novo deve ser diferente do valor anterior");
        }
    }
}