package com.annaehugo.freepharma.application.dto.administrativo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnidadeDTO {
    private Long id;
    private String tipo;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String inscricaoEstadual;
    private String endereco;
    private String telefone;
    private String email;
    private String status;
    private Date ultimoAcesso;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    private Long farmaciaId;
    private Long responsavelLocalId;
}