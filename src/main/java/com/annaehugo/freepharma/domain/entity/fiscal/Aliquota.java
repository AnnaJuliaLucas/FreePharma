package com.annaehugo.freepharma.domain.entity.fiscal;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import com.annaehugo.freepharma.domain.entity.estoque.ProdutoReferencia;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
// Sem suporte a CRUD
public class Aliquota extends EntidadeBase {
    @ManyToOne
    @JoinColumn(name = "produto_id")
    private com.annaehugo.freepharma.domain.entity.estoque.ProdutoReferencia produto;

    private String tipoImposto;
    private BigDecimal percentual;
    private Date vigenciaInicio;
    private Date vigenciaFim;

    @ManyToOne
    @JoinColumn(name = "produto_referencia_id")
    private ProdutoReferencia produtoReferencia;
}
