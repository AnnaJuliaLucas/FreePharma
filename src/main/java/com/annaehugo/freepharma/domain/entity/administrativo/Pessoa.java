package com.annaehugo.freepharma.domain.entity.administrativo;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class Pessoa extends EntidadeBase {
    private String nome;
    private String cpfCnpj;
    private String email;
    private String telefone;
}