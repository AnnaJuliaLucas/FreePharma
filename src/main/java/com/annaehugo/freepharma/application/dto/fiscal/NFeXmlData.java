package com.annaehugo.freepharma.application.dto.fiscal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DTO para representar os dados extraídos de um XML de NFe
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NFeXmlData {
    private String chaveAcesso;
    private String numero;
    private String serie;
    private Date dataEmissao;
    private BigDecimal valorTotal;
    private String tipoOperacao; // "COMPRA" ou "VENDA"
    
    private EmitenteDados emitente;
    private DestinatarioDados destinatario;
    private List<ItemNFeDados> itens = new ArrayList<>();
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmitenteDados {
        private String cnpj;
        private String razaoSocial;
        private String nomeFantasia;
        private String inscricaoEstadual;
        private String endereco;
        private String telefone;
        private String email;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DestinatarioDados {
        private String cnpjCpf;
        private String nome;
        private String endereco;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemNFeDados {
        private String codigoProduto;
        private String nomeProduto;
        private String descricaoProduto;
        private String ean;
        private String ncm;
        private String cfop;
        private String unidadeMedida;
        private Integer quantidade;
        private BigDecimal valorUnitario;
        private BigDecimal valorTotal;
        private String lote;
        private Date dataVencimento;
    }
}