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
public class AjusteEstoqueProdutoDTO {
    private Long id;
    private Date dataAjuste;
    private Integer quantidadeAnterior;
    private Integer quantidadeNova;
    private Integer quantidadeAjuste;
    private long valorUnitarioAnterior;
    private long valorUnitarioNovo;
    private String tipoAjuste;
    private String motivo;
    private String observacoes;
    private String numeroDocumento;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    private Long estoqueProdutoId;
    private EstoqueProdutoDTO estoqueProduto;
    private Long usuarioResponsavelId;
    private UsuarioAdministradorDTO usuarioResponsavel;
}