package com.annaehugo.freepharma.domain.entity.fiscal;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.Entity;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Sem suporte a CRUD
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioFiscal extends EntidadeBase {
    private String tipoRelatorio;
    private Date periodoInicio;
    private Date periodoFim;
}
