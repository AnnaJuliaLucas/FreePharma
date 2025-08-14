package com.annaehugo.freepharma.domain.entity.estoque;

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
public class Categoria extends EntidadeBase {
    
    @Column(nullable = false, length = 50)
    private String codigo;
    
    @Column(nullable = false, length = 150)
    private String nome;
    
    @Column(length = 500)
    private String descricao;
    
    @Column(nullable = false)
    private Boolean ativo = true;
    
    @ManyToOne
    @JoinColumn(name = "categoria_pai_id")
    private Categoria categoriaPai;
    
    @OneToMany(mappedBy = "categoriaPai")
    private List<Categoria> subcategorias;
    
    @OneToMany(mappedBy = "categoria")
    private List<ProdutoReferencia> produtos;
}