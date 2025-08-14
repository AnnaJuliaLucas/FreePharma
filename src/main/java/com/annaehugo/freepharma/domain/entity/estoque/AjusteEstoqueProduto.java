package com.annaehugo.freepharma.domain.entity.estoque;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador;
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
public class AjusteEstoqueProduto extends EntidadeBase {
    
    @ManyToOne
    @JoinColumn(name = "estoque_produto_id")
    private EstoqueProduto estoqueProduto;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioAdministrador usuarioResponsavel;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAjuste;
    
    @Column(nullable = false)
    private Integer quantidadeAnterior;
    
    @Column(nullable = false)
    private Integer quantidadeNova;
    
    @Column(nullable = false)
    private Integer quantidadeAjuste;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal valorUnitarioAnterior;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal valorUnitarioNovo;
    
    @Column(nullable = false, length = 50)
    private String tipoAjuste;
    
    @Column(nullable = false, length = 1000)
    private String motivo;
    
    @Column(length = 1000)
    private String observacoes;
    
    @Column(length = 50)
    private String numeroDocumento;
} 