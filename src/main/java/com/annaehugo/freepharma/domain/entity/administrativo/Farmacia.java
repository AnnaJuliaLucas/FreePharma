package com.annaehugo.freepharma.domain.entity.administrativo;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import com.annaehugo.freepharma.domain.entity.fiscal.ConfiguracaoFiscal;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Farmacia extends EntidadeBase {
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String inscricaoEstadual;
    private String inscricaoMunicipal;
    private String endereco;
    private String telefoneContato;
    private String emailContato;
    private String status;

    @OneToOne
    @JoinColumn(name = "configuracao_fiscal_id")
    private ConfiguracaoFiscal configuracaoFiscal;

    @OneToMany(mappedBy = "farmacia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Responsavel> responsaveis = new ArrayList<>();

}

