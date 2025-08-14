package com.annaehugo.freepharma.domain.repository.estoque;

import com.annaehugo.freepharma.domain.entity.estoque.Fornecedor;
import com.annaehugo.freepharma.domain.entity.estoque.ProdutoFornecedor;
import com.annaehugo.freepharma.domain.entity.estoque.ProdutoReferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoFornecedorRepository extends JpaRepository<ProdutoFornecedor, Long> {
    Optional<ProdutoFornecedor> findByProdutoReferenciaAndFornecedor(ProdutoReferencia produtoReferencia, Fornecedor fornecedor);
    
    List<ProdutoFornecedor> findByFornecedor(Fornecedor fornecedor);
    List<ProdutoFornecedor> findByProdutoReferencia(ProdutoReferencia produtoReferencia);
    List<ProdutoFornecedor> findByAtivoTrue();
}
