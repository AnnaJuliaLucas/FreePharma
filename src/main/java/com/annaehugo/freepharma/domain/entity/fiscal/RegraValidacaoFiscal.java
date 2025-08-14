package com.annaehugo.freepharma.domain.entity.fiscal;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegraValidacaoFiscal extends EntidadeBase {
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(nullable = false, length = 1000)
    private String descricao;
    
    @Column(nullable = false, length = 50)
    private String modulo;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoInconsistencia tipoInconsistencia;
    
    @Column(nullable = false, length = 20)
    private String severidade = "MEDIA";
    
    @Column(nullable = false)
    private Boolean ativo = true;
    
    @Column(nullable = false)
    private Boolean bloqueante = false;
    
    @Column(columnDefinition = "TEXT")
    private String condicaoValidacao;
    
    @Column(length = 1000)
    private String mensagemErro;
    
    @Column(length = 1000)
    private String sugestaoCorrecao;
    
    @Column(nullable = false)
    private Integer ordemExecucao = 100;
}