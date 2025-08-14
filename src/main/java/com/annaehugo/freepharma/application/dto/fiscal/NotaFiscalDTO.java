package com.annaehugo.freepharma.application.dto.fiscal;

import com.annaehugo.freepharma.application.dto.administrativo.ClienteDTO;
import com.annaehugo.freepharma.application.dto.administrativo.UnidadeDTO;
import com.annaehugo.freepharma.application.dto.estoque.FornecedorDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaFiscalDTO {
    private Long id;
    private String numero;
    private String chaveAcesso;
    private String hashAssinatura;
    private String status;
    private Date dataEmissao;
    private BigDecimal valorTotal;
    private Long farmaciaId;
    private String tipoOperacao;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    private FornecedorDTO fornecedor;
    private ClienteDTO cliente;
    private UnidadeDTO unidade;
    private Long loteProcessamentoId;
    private Long relatorioFiscalId;
    private Long importacaoNFeId;
    private List<NotaFiscalItemDTO> itens;
    private List<InconsistenciaDTO> inconsistencias;
}