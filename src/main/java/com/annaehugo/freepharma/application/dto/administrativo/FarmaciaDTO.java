package com.annaehugo.freepharma.application.dto.administrativo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmaciaDTO {
    private Long id;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String inscricaoEstadual;
    private String inscricaoMunicipal;
    private String endereco;
    private String telefoneContato;
    private String emailContato;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    private Long configuracaoFiscalId;
    private ResponsavelDTO responsavel;
}