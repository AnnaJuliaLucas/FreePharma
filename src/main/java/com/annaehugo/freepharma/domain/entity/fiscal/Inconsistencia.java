package com.annaehugo.freepharma.domain.entity.fiscal;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import com.annaehugo.freepharma.domain.entity.compartilhado.Notificacao;
import javax.persistence.*;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inconsistencia extends EntidadeBase {
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoInconsistencia tipo;
    
    @Column(nullable = false, length = 1000)
    private String descricao;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataDeteccao;
    
    @Column(nullable = false, length = 50)
    private String status = "PENDENTE";
    
    @Column(length = 50)
    private String severidade = "MEDIA";

    @Column(length = 500)
    private String observacaoResolucao;
    
    @Column(length = 1000)
    private String sugestaoCorrecao;
    
    @Column(length = 1000)
    private String observacoes;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataResolucao;

    @ManyToOne
    @JoinColumn(name = "nota_fiscal_id")
    private NotaFiscal notaFiscal;
    
    @ManyToOne
    @JoinColumn(name = "importacao_nfe_id")
    private ImportacaoNFe importacaoNFe;

    @OneToMany(mappedBy = "inconsistencia", cascade = CascadeType.ALL)
    private List<Notificacao> notificacoes;
}
