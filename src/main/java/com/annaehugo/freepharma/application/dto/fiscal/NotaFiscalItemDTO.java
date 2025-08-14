package com.annaehugo.freepharma.application.dto.fiscal;

import com.annaehugo.freepharma.application.dto.estoque.ProdutoReferenciaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaFiscalItemDTO {
    private Long id;
    private BigDecimal valorUnitario;
    private String descricao;
    private BigDecimal quantidade;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    private Long notaFiscalId;
    private ProdutoReferenciaDTO produtoReferencia;
}