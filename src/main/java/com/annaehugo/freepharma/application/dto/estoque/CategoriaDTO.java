package com.annaehugo.freepharma.application.dto.estoque;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    private Long id;
    private String codigo;
    private String nome;
    private String descricao;
    private Boolean ativo;
    private Date createdAt;
    private Date updatedAt;
    private Long categoriaPaiId;
    private List<CategoriaDTO> subcategorias;
}