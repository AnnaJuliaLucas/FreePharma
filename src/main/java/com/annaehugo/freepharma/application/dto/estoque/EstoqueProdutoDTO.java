package com.annaehugo.freepharma.application.dto.estoque;

import com.annaehugo.freepharma.application.dto.administrativo.UnidadeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstoqueProdutoDTO {
    private Long id;
    private Integer quantidadeAtual;
    private Integer estoqueMinimo;
    private Integer estoqueMaximo;
    private Integer pontoReposicao;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private String lote;
    private Date dataVencimento;
    private String localizacao;
    private Boolean bloqueado;
    private String motivoBloqueio;
    private Date dataUltimaMovimentacao;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    private Long produtoFornecedorId;
    private Long produtoReferenciaId;
    private UnidadeDTO unidade;
}