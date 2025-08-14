package com.annaehugo.freepharma.application.dto.estoque;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorDTO {
    private Long id;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String inscricaoEstadual;
    private String endereco;
    private String email;
    private String telefone;
    private String cidade;
    private String estado;
    private String cep;
    private String status;
    private Date dataCadastro;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
}