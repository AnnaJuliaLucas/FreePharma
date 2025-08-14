package com.annaehugo.freepharma.domain.repository.administrativo;

import com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioAdministradorRepository extends JpaRepository<UsuarioAdministrador, Long> {
    Optional<UsuarioAdministrador> findByLogin(String login);
    Optional<UsuarioAdministrador> findByEmail(String email);
    
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);

    List<UsuarioAdministrador> findByStatus(String status);
    List<UsuarioAdministrador> findByUnidadesAcessoId(Long unidadeId);
}
