package com.annaehugo.freepharma.domain.repository.administrativo;

import com.annaehugo.freepharma.domain.entity.administrativo.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissaoRepository extends JpaRepository<Permissao, Long> {
    
    List<Permissao> findByAtivoTrue();
    
    List<Permissao> findByModuloAndAtivoTrue(String modulo);
    
    Optional<Permissao> findByCodigo(String codigo);
    
    @Query("SELECT p FROM Permissao p WHERE p.modulo = :modulo AND p.ativo = true ORDER BY p.nome")
    List<Permissao> findPermissoesByModulo(@Param("modulo") String modulo);
}