package com.annaehugo.freepharma.application.dto.fiscal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoteProcessamentoDTO {
    private Long id;
    private String codigoLote;
    private String status;
    private Date dataInicio;
    private Date dataFim;
    private Integer totalNotasFiscais;
    private Integer notasProcessadas;
    private Integer notasSucesso;
    private Integer notasErro;
    private String observacoes;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    private Long unidadeId;
}