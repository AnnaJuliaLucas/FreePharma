package com.annaehugo.freepharma.domain.repository.administrativo;

import com.annaehugo.freepharma.domain.entity.administrativo.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    
    List<Perfil> findByAtivoTrue();
    @Query("SELECT p FROM Perfil p WHERE p.ativo = true")
    List<Perfil> findByAtivo();
    @Query("SELECT p FROM Perfil p JOIN p.permissoes perm WHERE perm.codigo = :codigoPermissao")
    List<Perfil> findByPermissao(String codigoPermissao);

    Optional<Perfil> findByNome(String nome);
    

    

}