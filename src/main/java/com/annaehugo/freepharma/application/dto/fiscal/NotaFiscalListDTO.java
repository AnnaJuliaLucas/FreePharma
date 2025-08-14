package com.annaehugo.freepharma.application.dto.fiscal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaFiscalListDTO {
    private Long id;
    private String numero;
    private String status;
    private Date dataEmissao;
    private BigDecimal valorTotal;
    private String tipoOperacao;
    private Date createdAt;
    private String fornecedorNome;
    private String clienteNome;
    private String unidadeNome;
    private boolean temInconsistencias;
    
    public String getChaveAcessoMascarada() {
        return "****-****-****-****";
    }
}