package com.annaehugo.freepharma.domain.repository.estoque;

import com.annaehugo.freepharma.domain.entity.estoque.ProdutoReferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoReferenciaRepository extends JpaRepository<ProdutoReferencia, Long> {
    List<ProdutoReferencia> findByStatus(String status);

    List<ProdutoReferencia> findByNome(String nome);
    
    Optional<ProdutoReferencia> findFirstByNome(String nome);

    Optional<ProdutoReferencia> findByCodigoInterno(String codigoInterno);

    Optional<ProdutoReferencia> findByEan(String ean);
}
