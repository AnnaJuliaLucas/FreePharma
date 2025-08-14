package com.annaehugo.freepharma.application.dto.administrativo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsavelDTO {
    private Long id;
    private String nome;
    private String cpfCnpj;
    private String email;
    private String telefone;
    private String registroProfissional;
    private Boolean status;
    private String crf;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    private Long farmaciaId;
}