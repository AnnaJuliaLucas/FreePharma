package com.annaehugo.freepharma.domain.entity.administrativo;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Perfil extends EntidadeBase {
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(length = 500)
    private String descricao;
    
    @Column(nullable = false)
    private Boolean ativo = true;
    
    @ManyToMany(mappedBy = "perfis")
    private List<UsuarioAdministrador> usuarios;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "perfil_permissao",
        joinColumns = @JoinColumn(name = "perfil_id"),
        inverseJoinColumns = @JoinColumn(name = "permissao_id")
    )
    private List<Permissao> permissoes;
}