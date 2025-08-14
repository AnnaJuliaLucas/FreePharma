package com.annaehugo.freepharma.domain.entity.administrativo;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario_administrador")
public class UsuarioAdministrador extends Pessoa {
    
    @Column(nullable = false, unique = true, length = 50)
    private String login;
    
    @Column(nullable = false)
    private String senha;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;
    
    @Column(nullable = false, length = 50)
    private String status = "ATIVO"; // ATIVO, INATIVO, BLOQUEADO
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date ultimoAcesso;
    
    private Boolean autenticacao2FA = false;
    
    private String secreto2FA;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_perfil",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "perfil_id")
    )
    private List<Perfil> perfis;
    
    @ManyToMany
    @JoinTable(
        name = "usuario_unidade_acesso",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "unidade_id")
    )
    private List<Unidade> unidadesAcesso;
    
    public boolean isAtivo() {
        return super.isAtivo();
    }
}
