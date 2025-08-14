package com.annaehugo.freepharma.domain.repository.estoque;

import com.annaehugo.freepharma.domain.entity.administrativo.Unidade;
import com.annaehugo.freepharma.domain.entity.estoque.EstoqueProduto;
import com.annaehugo.freepharma.domain.entity.estoque.ProdutoFornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueProdutoRepository extends JpaRepository<EstoqueProduto, Long> {

    List<EstoqueProduto> findByProdutoFornecedorId(Long produtoFornecedorId);
    List<EstoqueProduto> findByProdutoReferenciaId(Long produtoReferenciaId);
    Optional<EstoqueProduto> findByProdutoFornecedorIdAndUnidadeIdAndLote(Long produtoFornecedorId, Long unidadeId, String lote);
    List<EstoqueProduto> findByUnidadeId(Long unidadeId);
    
    Optional<EstoqueProduto> findByProdutoFornecedorAndUnidadeAndLote(ProdutoFornecedor produtoFornecedor, Unidade unidade, String lote);
}
