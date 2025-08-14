package com.annaehugo.freepharma.domain.entity.estoque;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Data  
@NoArgsConstructor
@AllArgsConstructor
public class Fornecedor extends EntidadeBase {
    
    @Column(nullable = false, length = 200)
    private String razaoSocial;
    
    @Column(length = 200)
    private String nomeFantasia;
    
    @Column(nullable = false, unique = true, length = 50)
    private String cnpj;
    
    @Column(length = 50)
    private String inscricaoEstadual;
    
    @Column(length = 500)
    private String endereco;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 50)
    private String telefone;

    @Column(length = 40)
    private String cidade;

    @Column(length = 40)
    private String estado;

    @Column(length = 40)
    private String cep;
    
    @Column(nullable = false, length = 50)
    private String status = "ATIVO";
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro = new Date();
}
