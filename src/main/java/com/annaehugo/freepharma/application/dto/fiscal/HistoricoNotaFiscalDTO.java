package com.annaehugo.freepharma.application.dto.fiscal;

import com.annaehugo.freepharma.application.dto.administrativo.UsuarioAdministradorDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoNotaFiscalDTO {
    private Long id;
    private Date dataAlteracao;
    private Date dataOperacao;
    private String campoAlterado;
    private String valorAnterior;
    private String valorNovo;
    private String tipoOperacao;
    private String statusAnterior;
    private String statusNovo;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    private Long notaFiscalId;
    private Long usuarioResponsavelId;
    private UsuarioAdministradorDTO usuarioResponsavel;
}