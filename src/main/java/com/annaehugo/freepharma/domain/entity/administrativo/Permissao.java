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
public class Permissao extends EntidadeBase {
    
    @Column(nullable = false, unique = true, length = 100)
    private String codigo;
    
    @Column(nullable = false, length = 150)
    private String nome;
    
    @Column(length = 500)
    private String descricao;
    
    @Column(nullable = false, length = 50)
    private String modulo; // ADMINISTRATIVO, ESTOQUE, FISCAL
    
    @Column(nullable = false)
    private Boolean ativo = true;
    
    @ManyToMany(mappedBy = "permissoes")
    private List<Perfil> perfis;
}