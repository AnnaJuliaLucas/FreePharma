package com.annaehugo.freepharma.domain.entity.administrativo;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;

import javax.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends EntidadeBase {
    private String nome;
    private String cpfCnpj;
    private String endereco;
    private String email;
    private String telefone;
    private String status;
}
