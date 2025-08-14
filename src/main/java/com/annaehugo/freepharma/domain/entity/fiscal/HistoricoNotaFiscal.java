package com.annaehugo.freepharma.domain.entity.fiscal;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscal;
import com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador;
import javax.persistence.*;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoNotaFiscal extends EntidadeBase {
    private Date dataAlteracao;
    private Date dataOperacao;
    private String campoAlterado;
    private String valorAnterior;
    private String valorNovo;
    private String tipoOperacao;
    private String statusAnterior;
    private String statusNovo;

    @ManyToOne
    @JoinColumn(name = "nota_fiscal_id")
    private NotaFiscal notaFiscal;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador usuarioResponsavel;
}
