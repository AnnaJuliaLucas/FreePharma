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
public class ProdutoReferenciaDTO {
    private Long id;
    private String codigoInterno;
    private String nome;
    private String descricao;
    private String ean;
    private String ncm;
    private String cfop;
    private String unidadeMedida;
    private BigDecimal peso;
    private BigDecimal altura;
    private BigDecimal largura;
    private BigDecimal profundidade;
    private Date validade;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    private CategoriaDTO categoria;
    private List<ProdutoFornecedorDTO> produtosFornecedor;
}