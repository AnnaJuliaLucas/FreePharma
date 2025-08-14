package com.annaehugo.freepharma.domain.entity.fiscal;

public enum TipoInconsistencia {
    PRODUTO_NAO_CADASTRADO("Produto não cadastrado no sistema"),
    PRECO_DIVERGENTE("Preço divergente do cadastrado"),
    FORNECEDOR_INVALIDO("Fornecedor inválido ou não cadastrado"),
    CFOP_INCORRETO("CFOP incorreto para a operação"),
    NCM_INVALIDO("NCM inválido ou inconsistente"),
    CLIENTE_NAO_CADASTRADO("Cliente não cadastrado"),
    VALOR_TOTAL_DIVERGENTE("Valor total da nota divergente"),
    ALIQUOTA_INCORRETA("Alíquota de imposto incorreta"),
    DATA_EMISSAO_INVALIDA("Data de emissão inválida"),
    CHAVE_ACESSO_DUPLICADA("Chave de acesso já existe"),
    ESTOQUE_INSUFICIENTE("Estoque insuficiente para a operação"),
    UNIDADE_MEDIDA_DIVERGENTE("Unidade de medida divergente");
    
    private final String descricao;
    
    TipoInconsistencia(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}