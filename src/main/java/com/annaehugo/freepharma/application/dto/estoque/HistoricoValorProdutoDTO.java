package com.annaehugo.freepharma.application.dto.estoque;

import com.annaehugo.freepharma.application.dto.administrativo.UsuarioAdministradorDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoValorProdutoDTO {
    private Long id;
    private Date dataAlteracao;
    private BigDecimal valorAnterior;
    private BigDecimal valorNovo;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    private Long estoqueProdutoId;
    private EstoqueProdutoDTO estoqueProduto;
    private Long usuarioResponsavelId;
    private UsuarioAdministradorDTO usuarioResponsavel;
}