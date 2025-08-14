package com.annaehugo.freepharma.domain.entity.estoque;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class ProdutoReferencia extends EntidadeBase {
    
    @Column(nullable = false, unique = true, length = 50)
    private String codigoInterno;
    
    @Column(nullable = false, length = 200)
    private String nome;
    
    @Column(length = 1000)
    private String descricao;
    
    @Column(length = 20)
    private String ean;
    
    @Column(length = 10)
    private String ncm;
    
    @Column(length = 10)
    private String cfop;
    
    @Column(length = 10)
    private String unidadeMedida = "UN";

    @Temporal(TemporalType.DATE)
    private Date validade;
    
    @Column(nullable = false, length = 50)
    private String status = "ATIVO";
    
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    
    @OneToMany(mappedBy = "produtoReferencia")
    private List<ProdutoFornecedor> produtosFornecedor;
}
