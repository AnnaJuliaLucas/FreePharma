package com.annaehugo.freepharma.application.dto.administrativo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilDTO {
    private Long id;
    private String nome;
    private String descricao;
    private Boolean ativo;
    private Date createdAt;
    private Date updatedAt;
    private List<PermissaoDTO> permissoes;
}