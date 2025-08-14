package com.annaehugo.freepharma.application.dto.compartilhado;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoDTO {
    private Long id;
    private String tipo;
    private String titulo;
    private String mensagem;
    private Date dataEnvio;
    private Date dataLeitura;
    private String status;
    private String prioridade;
    private String linkAcao;
    private String textoAcao;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    private Long inconsistenciaId;
    private Long destinatarioId;
}