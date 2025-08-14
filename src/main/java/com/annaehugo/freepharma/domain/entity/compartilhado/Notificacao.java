package com.annaehugo.freepharma.domain.entity.compartilhado;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador;
import com.annaehugo.freepharma.domain.entity.fiscal.Inconsistencia;
import javax.persistence.*;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacao extends EntidadeBase {
    
    @Column(nullable = false, length = 50)
    private String tipo;
    
    @Column(nullable = false, length = 100)
    private String titulo;
    
    @Column(nullable = false, length = 1000)
    private String mensagem;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEnvio;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLeitura;
    
    @Column(nullable = false, length = 20)
    private String status = "PENDENTE";
    
    @Column(nullable = false, length = 20)
    private String prioridade = "NORMAL";
    
    @Column(length = 1000)
    private String linkAcao;
    
    @Column(length = 100)
    private String textoAcao;

    @ManyToOne
    @JoinColumn(name = "inconsistencia_id")
    private Inconsistencia inconsistencia;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private UsuarioAdministrador destinatario;
}
