package com.annaehugo.freepharma.domain.entity.fiscal;

public enum StatusImportacao {
    PENDENTE("Aguardando processamento"),
    PROCESSANDO("Em processamento"),
    CONCLUIDA("Processamento concluído"),
    ERRO("Erro no processamento"),
    CANCELADA("Importação cancelada");
    
    private final String descricao;
    
    StatusImportacao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}