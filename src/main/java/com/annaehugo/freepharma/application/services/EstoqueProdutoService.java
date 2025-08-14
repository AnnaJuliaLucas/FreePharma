package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.estoque.EstoqueProduto;
import com.annaehugo.freepharma.domain.entity.estoque.AjusteEstoqueProduto;
import com.annaehugo.freepharma.domain.repository.estoque.EstoqueProdutoRepository;
import com.annaehugo.freepharma.domain.repository.estoque.AjusteEstoqueRepository;
import com.annaehugo.freepharma.domain.repository.estoque.ProdutoReferenciaRepository;
import com.annaehugo.freepharma.domain.repository.administrativo.UnidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EstoqueProdutoService {

    private final EstoqueProdutoRepository estoqueProdutoRepository;
    private final AjusteEstoqueRepository ajusteEstoqueRepository;
    private final ProdutoReferenciaRepository produtoReferenciaRepository;
    private final UnidadeRepository unidadeRepository;

    @Autowired
    public EstoqueProdutoService(
            EstoqueProdutoRepository estoqueProdutoRepository,
            AjusteEstoqueRepository ajusteEstoqueRepository,
            ProdutoReferenciaRepository produtoReferenciaRepository,
            UnidadeRepository unidadeRepository) {
        this.estoqueProdutoRepository = estoqueProdutoRepository;
        this.ajusteEstoqueRepository = ajusteEstoqueRepository;
        this.produtoReferenciaRepository = produtoReferenciaRepository;
        this.unidadeRepository = unidadeRepository;
    }

    public List<EstoqueProduto> listarTodos() {
        return estoqueProdutoRepository.findAll();
    }

    public List<EstoqueProduto> listarPorUnidade(Long unidadeId) {
        return estoqueProdutoRepository.findByUnidadeId(unidadeId);
    }

    public List<EstoqueProduto> listarPorProduto(Long produtoReferenciaId, Long unidadeId) {
        return estoqueProdutoRepository.findByProdutoReferenciaId(produtoReferenciaId);
    }

    public Optional<EstoqueProduto> buscarPorId(Long id) {
        return estoqueProdutoRepository.findById(id);
    }

    public Optional<EstoqueProduto> buscarPorProdutoUnidadeLote(Long produtoFornecedorId, Long unidadeId, String lote) {
        return estoqueProdutoRepository.findByProdutoFornecedorIdAndUnidadeIdAndLote(produtoFornecedorId, unidadeId, lote);
    }

    public EstoqueProduto salvar(EstoqueProduto produtoEstoque) {
        validarDadosEstoqueProduto(produtoEstoque);
        
        if (produtoEstoque.getProdutoFornecedor() == null || produtoEstoque.getProdutoFornecedor().getId() == null) {
            throw new RuntimeException("Produto fornecedor é obrigatório");
        }
        
        if (!unidadeRepository.existsById(produtoEstoque.getUnidade().getId())) {
            throw new RuntimeException("Unidade não encontrada");
        }
        
        if (produtoEstoque.getValorUnitario() != null && produtoEstoque.getQuantidadeAtual() != null) {
            BigDecimal valorTotal = produtoEstoque.getValorUnitario()
                    .multiply(BigDecimal.valueOf(produtoEstoque.getQuantidadeAtual()));
            produtoEstoque.setValorTotal(valorTotal);
        }
        
        produtoEstoque.setDataUltimaMovimentacao(new Date());
        return estoqueProdutoRepository.save(produtoEstoque);
    }

    public EstoqueProduto atualizar(Long id, EstoqueProduto produtoEstoqueAtualizado) {
        return estoqueProdutoRepository.findById(id)
                .map(produtoEstoque -> {
                    produtoEstoqueAtualizado.setId(id);
                    validarDadosEstoqueProduto(produtoEstoqueAtualizado);

                    if (produtoEstoqueAtualizado.getValorUnitario() != null && produtoEstoqueAtualizado.getQuantidadeAtual() != null) {
                        BigDecimal valorTotal = produtoEstoqueAtualizado.getValorUnitario()
                                .multiply(BigDecimal.valueOf(produtoEstoqueAtualizado.getQuantidadeAtual()));
                        produtoEstoqueAtualizado.setValorTotal(valorTotal);
                    }
                    
                    produtoEstoqueAtualizado.setDataUltimaMovimentacao(new Date());
                    return estoqueProdutoRepository.save(produtoEstoqueAtualizado);
                })
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
    }

    public void deletar(Long id) {
        if (!estoqueProdutoRepository.existsById(id)) {
            throw new RuntimeException("Estoque não encontrado");
        }
        estoqueProdutoRepository.deleteById(id);
    }

    public EstoqueProduto ajustarEstoque(Long id, Integer novaQuantidade, String motivo, Long usuarioId) {
        EstoqueProduto produtoEstoque = estoqueProdutoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
        
        Integer quantidadeAnterior = produtoEstoque.getQuantidadeAtual();
        Integer quantidadeAjuste = novaQuantidade - quantidadeAnterior;

        AjusteEstoqueProduto ajuste = new AjusteEstoqueProduto();
        ajuste.setEstoqueProduto(produtoEstoque);
        ajuste.setDataAjuste(new Date());
        ajuste.setQuantidadeAnterior(quantidadeAnterior);
        ajuste.setQuantidadeNova(novaQuantidade);
        ajuste.setQuantidadeAjuste(quantidadeAjuste);
        ajuste.setTipoAjuste(quantidadeAjuste > 0 ? "ENTRADA" : "SAIDA");
        ajuste.setMotivo(motivo);
        ajuste.setValorUnitarioAnterior(produtoEstoque.getValorUnitario());
        ajuste.setValorUnitarioNovo(produtoEstoque.getValorUnitario());
        
        ajusteEstoqueRepository.save(ajuste);
        
        // Atualizar estoque
        produtoEstoque.setQuantidadeAtual(novaQuantidade);
        produtoEstoque.setDataUltimaMovimentacao(new Date());
        
        // Recalcular valor total
        if (produtoEstoque.getValorUnitario() != null) {
            BigDecimal valorTotal = produtoEstoque.getValorUnitario()
                    .multiply(BigDecimal.valueOf(novaQuantidade));
            produtoEstoque.setValorTotal(valorTotal);
        }
        
        return estoqueProdutoRepository.save(produtoEstoque);
    }

    public EstoqueProduto bloquearEstoque(Long id, String motivo) {
        return estoqueProdutoRepository.findById(id)
                .map(produtoEstoque -> {
                    produtoEstoque.setBloqueado(true);
                    produtoEstoque.setMotivoBloqueio(motivo);
                    return estoqueProdutoRepository.save(produtoEstoque);
                })
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
    }

    public EstoqueProduto desbloquearEstoque(Long id) {
        return estoqueProdutoRepository.findById(id)
                .map(produtoEstoque -> {
                    produtoEstoque.setBloqueado(false);
                    produtoEstoque.setMotivoBloqueio(null);
                    return estoqueProdutoRepository.save(produtoEstoque);
                })
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
    }

    public List<AjusteEstoqueProduto> listarAjustesPorEstoque(Long produtoEstoqueId) {
        return ajusteEstoqueRepository.findByEstoqueProdutoId(produtoEstoqueId);
    }

    private void validarDadosEstoqueProduto(EstoqueProduto produtoEstoque) {
//        if (produtoEstoque.getProdutoReferencia() == null || produtoEstoque.getProdutoReferencia().getId() == null) {
//            throw new RuntimeException("Produto é obrigatório");
//        }
        if (produtoEstoque.getProdutoFornecedor() == null || produtoEstoque.getProdutoFornecedor().getId() == null) {
            throw new RuntimeException("Produto fornecedor é obrigatório");
        }
        
        if (produtoEstoque.getUnidade() == null || produtoEstoque.getUnidade().getId() == null) {
            throw new RuntimeException("Unidade é obrigatória");
        }
        
        if (produtoEstoque.getQuantidadeAtual() == null || produtoEstoque.getQuantidadeAtual() < 0) {
            throw new RuntimeException("Quantidade atual deve ser maior ou igual a zero");
        }
        
        if (produtoEstoque.getEstoqueMinimo() == null || produtoEstoque.getEstoqueMinimo() < 0) {
            throw new RuntimeException("Estoque mínimo deve ser maior ou igual a zero");
        }
        
        if (produtoEstoque.getEstoqueMaximo() == null || produtoEstoque.getEstoqueMaximo() < 0) {
            throw new RuntimeException("Estoque máximo deve ser maior ou igual a zero");
        }
        
        if (produtoEstoque.getEstoqueMaximo() < produtoEstoque.getEstoqueMinimo()) {
            throw new RuntimeException("Estoque máximo não pode ser menor que o estoque mínimo");
        }
        
        if (produtoEstoque.getValorUnitario() == null || produtoEstoque.getValorUnitario().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Valor unitário deve ser maior ou igual a zero");
        }
    }
}