package com.annaehugo.freepharma.domain.entity.estoque;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"produto_referencia_id", "fornecedor_id"})
})
public class ProdutoFornecedor extends EntidadeBase {
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoCompra = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda = BigDecimal.ZERO;
    
    @Column(nullable = false, length = 100)
    private String codigoFornecedor;
    
    @Column(length = 200)
    private String nomeFornecedor;
    
    @Column(length = 50)
    private String unidadeMedidaFornecedor;
    
    @Column(length = 20)
    private String eanFornecedor;
    
    @Temporal(TemporalType.DATE)
    private Date dataUltimaCompra;
    
    @Column(nullable = false)
    private Boolean ativo = true;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_referencia_id")
    private ProdutoReferencia produtoReferencia;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;
    
    @OneToMany(mappedBy = "produtoFornecedor")
    private List<EstoqueProduto> estoques;
}
