package com.annaehugo.freepharma.application.dto.administrativo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissaoDTO {
    private Long id;
    private String codigo;
    private String nome;
    private String descricao;
    private String modulo;
    private Boolean ativo;
    private Date createdAt;
    private Date updatedAt;
}