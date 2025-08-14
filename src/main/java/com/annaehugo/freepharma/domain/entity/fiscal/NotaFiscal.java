package com.annaehugo.freepharma.domain.entity.fiscal;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaFiscal extends EntidadeBase {
    private String numero;
    private String chaveAcesso;
    private String hashAssinatura;
    private String status;
    private Date dataEmissao;
    private BigDecimal valorTotal;
    private Long farmaciaId;
    private String tipoOperacao;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private com.annaehugo.freepharma.domain.entity.estoque.Fornecedor fornecedor;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private com.annaehugo.freepharma.domain.entity.administrativo.Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "unidade_id")
    private com.annaehugo.freepharma.domain.entity.administrativo.Unidade unidade;

    @ManyToOne
    @JoinColumn(name = "lote_processamento_id")
    private LoteProcessamento loteProcessamento;

    @OneToMany(mappedBy = "notaFiscal", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<NotaFiscalItem> itens;

    @OneToMany(mappedBy = "notaFiscal", cascade = CascadeType.ALL)
    private List<Inconsistencia> inconsistencias;
    
    @ManyToOne
    @JoinColumn(name = "importacao_nfe_id")
    private ImportacaoNFe importacaoNFe;
}