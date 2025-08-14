package com.annaehugo.freepharma.domain.entity.fiscal;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.annaehugo.freepharma.domain.entity.estoque.ProdutoReferencia;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaFiscalItem extends EntidadeBase {
    private BigDecimal valorUnitario;
    private String descricao;
    private BigDecimal quantidade;

    @ManyToOne
    @JoinColumn(name = "nota_fiscal_id")
    private NotaFiscal notaFiscal;

    @ManyToOne
    @JoinColumn(name = "produto_referencia_id")
    private ProdutoReferencia produtoReferencia;

}
