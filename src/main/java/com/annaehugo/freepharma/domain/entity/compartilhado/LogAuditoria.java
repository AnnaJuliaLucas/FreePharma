package com.annaehugo.freepharma.domain.entity.compartilhado;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogAuditoria extends EntidadeBase {
    
    @Column(nullable = false)
    private Long usuarioId;
    
    @Column(nullable = false, length = 100)
    private String nomeUsuario;
    
    @Column(nullable = false, length = 50)
    private String acao;
    
    @Column(nullable = false, length = 100)
    private String entidadeAfetada; // Nome da classe/tabela
    
    private Long entidadeId; // ID do registro afetado
    
    @Column(columnDefinition = "TEXT")
    private String valoresAnteriores;
    
    @Column(columnDefinition = "TEXT")
    private String valoresNovos;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAcao;
    
    @Column(length = 45)
    private String enderecoIp;
    
    @Column(length = 500)
    private String userAgent;
    
    @Column(length = 1000)
    private String observacoes;
}