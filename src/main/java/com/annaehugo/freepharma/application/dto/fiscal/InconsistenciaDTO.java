package com.annaehugo.freepharma.application.dto.fiscal;

import com.annaehugo.freepharma.application.dto.compartilhado.NotificacaoDTO;
import com.annaehugo.freepharma.domain.entity.fiscal.TipoInconsistencia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InconsistenciaDTO {
    private Long id;
    private TipoInconsistencia tipo;
    private String descricao;
    private Date dataDeteccao;
    private String status;
    private String severidade;
    private String observacaoResolucao;
    private String sugestaoCorrecao;
    private String observacoes;
    private Date dataResolucao;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    private Long notaFiscalId;
    private Long importacaoNFeId;
    private List<NotificacaoDTO> notificacoes;
}