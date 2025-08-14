package com.annaehugo.freepharma.domain.entity.estoque;

import com.annaehugo.freepharma.domain.entity.administrativo.*;
import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"produto_fornecedor_id", "unidade_id", "lote"})
})
public class EstoqueProduto extends EntidadeBase {

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_fornecedor_id")
    private ProdutoFornecedor produtoFornecedor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_referencia_id")
    private ProdutoReferencia produtoReferencia;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;

    @Column(nullable = false)
    private Integer quantidadeAtual = 0;

    @Column(nullable = false)
    private Integer estoqueMinimo = 0;

    @Column(nullable = false)
    private Integer estoqueMaximo = 0;

    @Column(nullable = false)
    private Integer pontoReposicao = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(length = 50)
    private String lote;

    @Temporal(TemporalType.DATE)
    private Date dataVencimento;

    @Column(length = 100)
    private String localizacao;

    @Column(nullable = false)
    private Boolean bloqueado = false;

    @Column(length = 500)
    private String motivoBloqueio;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataUltimaMovimentacao;

    @OneToMany(mappedBy = "estoqueProduto")
    @JsonIgnore
    private List<AjusteEstoqueProduto> ajustes;

    @OneToMany(mappedBy = "estoqueProduto")
    @JsonIgnore
    private List<HistoricoValorProduto> historicoValores;

    public void setProdutoFornecedor(ProdutoFornecedor produtoFornecedor) {
        this.produtoFornecedor = produtoFornecedor;
        if(produtoFornecedor != null){
            this.produtoReferencia = produtoFornecedor.getProdutoReferencia();
        }
    }
    public ProdutoFornecedor getProdutoFornecedor() {
        return this.produtoFornecedor;
    }
}
