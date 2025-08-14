package com.annaehugo.freepharma.domain.entity.fiscal;

import com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador;
import com.annaehugo.freepharma.domain.entity.administrativo.Unidade;
import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportacaoNFe extends EntidadeBase {
    
    @Column(nullable = false, length = 500)
    private String nomeArquivo;
    
    @Column(nullable = false, length = 1000)
    private String caminhoArquivo;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusImportacao status = StatusImportacao.PENDENTE;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataImportacao;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInicioProcessamento;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataFimProcessamento;
    
    @Column(nullable = false)
    private Integer quantidadeNotasArquivo = 0;
    
    @Column(nullable = false)
    private Integer quantidadeNotasProcessadas = 0;
    
    @Column(nullable = false)
    private Integer quantidadeNotasComErro = 0;
    
    @Column(nullable = false)
    private Integer quantidadeInconsistenciasDetectadas = 0;
    
    @Column(columnDefinition = "TEXT")
    private String logProcessamento;
    
    @Column(columnDefinition = "TEXT")
    private String errosProcessamento;
    
    @Column(length = 1000)
    private String observacoes;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_importacao_id")
    private UsuarioAdministrador usuarioImportacao;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;
    
    @ManyToOne
    @JoinColumn(name = "lote_processamento_id")
    private LoteProcessamento loteProcessamento;
    
    @OneToMany(mappedBy = "importacaoNFe")
    private List<NotaFiscal> notasFiscais;
    
    @OneToMany(mappedBy = "importacaoNFe")
    private List<Inconsistencia> inconsistencias;
}