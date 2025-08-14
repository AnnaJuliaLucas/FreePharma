package com.annaehugo.freepharma.domain.repository.estoque;

import com.annaehugo.freepharma.domain.entity.estoque.AjusteEstoqueProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AjusteEstoqueRepository extends JpaRepository<AjusteEstoqueProduto, Long> {
    List<AjusteEstoqueProduto> findByEstoqueProdutoId(Long estoqueProdutoId);
}
