package com.annaehugo.freepharma.domain.repository.estoque;

import com.annaehugo.freepharma.domain.entity.estoque.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    Optional<Fornecedor> findByCnpj(String cnpj);
    Optional<Fornecedor> findByEmail(String email);

    boolean existsByCnpj(String cnpj);
    boolean existsByEmail(String email);
    
    List<Fornecedor> findByStatus(String status);
    List<Fornecedor> findByRazaoSocial(String razaoSocial);
    List<Fornecedor> findByAtivoTrue();
    List<Fornecedor> findByNomeFantasia(String nomeFantasia);

}
