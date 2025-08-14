package com.annaehugo.freepharma.domain.entity.fiscal;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.Entity;
import java.util.Date;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
// Sem suporte a CRUD
public class LoteProcessamento extends EntidadeBase {
    private UUID idLote;
    private Date dataInicio;
    private Date dataFim;
    private String status;
    private String tipoLote;
    private Integer progresso;
    private String detalhesErro;
}
