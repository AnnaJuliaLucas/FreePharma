package com.annaehugo.freepharma.application.dto.estoque;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoFornecedorDTO {
    private Long id;
    private BigDecimal precoCompra;
    private BigDecimal precoVenda;
    private String codigoFornecedor;
    private String nomeFornecedor;
    private String unidadeMedidaFornecedor;
    private String eanFornecedor;
    private Date dataUltimaCompra;
    private Boolean ativo;
    private Date createdAt;
    private Date updatedAt;
    private Long produtoReferenciaId;
    private Long fornecedorId;
    private FornecedorDTO fornecedor;
    private List<EstoqueProdutoDTO> estoques;
}