package com.annaehugo.freepharma.domain.entity.administrativo;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "unidade")
public class Unidade extends EntidadeBase {
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

    @ManyToOne
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;

    @OneToOne
    @JoinColumn(name = "responsavel_id")
    private Responsavel responsavelLocal;
}
