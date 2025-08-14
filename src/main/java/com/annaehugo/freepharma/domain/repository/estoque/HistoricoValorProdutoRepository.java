package com.annaehugo.freepharma.domain.repository.estoque;

import com.annaehugo.freepharma.domain.entity.estoque.HistoricoValorProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoValorProdutoRepository extends JpaRepository<HistoricoValorProduto, Long> {
}
