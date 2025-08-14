package com.annaehugo.freepharma.application.dto.fiscal;

import com.annaehugo.freepharma.domain.entity.fiscal.StatusImportacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportacaoNFeDTO {
    private Long id;
    private String nomeArquivo;
    private String tipoArquivo;
    private StatusImportacao status;
    private Date dataInicio;
    private Date dataFim;
    private Integer totalRegistros;
    private Integer registrosProcessados;
    private Integer registrosSucesso;
    private Integer registrosErro;
    private String mensagemErro;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    private Long unidadeId;
    private Long loteProcessamentoId;
}