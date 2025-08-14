package com.annaehugo.freepharma.domain.entity.compartilhado;

import com.annaehugo.freepharma.domain.entity.administrativo.Unidade;
import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricas extends EntidadeBase {
    
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataReferencia;
    
    @Column(nullable = false, length = 20)
    private String periodo;

    @Column(nullable = false)
    private Integer totalNotasProcessadas = 0;
    
    @Column(nullable = false)
    private Integer totalNotasComErro = 0;
    
    @Column(nullable = false)
    private Integer totalInconsistenciasDetectadas = 0;
    
    @Column(nullable = false)
    private Integer inconsistenciasPendentes = 0;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalVendas = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalCompras = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal margemLucroMedia = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer totalProdutosCadastrados = 0;
    
    @Column(nullable = false)
    private Integer produtosEstoqueBaixo = 0;
    
    @Column(nullable = false)
    private Integer produtosVencendo = 0;

    @Column(nullable = false)
    private BigDecimal tempoMedioProcessamento = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private BigDecimal taxaSucessoProcessamento = BigDecimal.ZERO;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCalculoMetricas;
    
    @ManyToOne
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;
}