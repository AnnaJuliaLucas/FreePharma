package com.annaehugo.freepharma.domain.entity.estoque;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
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

public class HistoricoValorProduto extends EntidadeBase {
    private Date dataAlteracao;
    private BigDecimal valorAnterior;
    private BigDecimal valorNovo;

    @ManyToOne
    @JoinColumn(name = "estoque_produto_id")
    private EstoqueProduto estoqueProduto;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador usuarioResponsavel;
}
