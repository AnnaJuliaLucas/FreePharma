package com.annaehugo.freepharma.domain.entity.estoque;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicamento extends ProdutoReferencia {
    
    @Column(length = 50)
    private String registroAnvisa;
    
    @Column(nullable = false, length = 200)
    private String principioAtivo;
    
    @Column(length = 100)
    private String concentracao;
    
    @Column(nullable = false, length = 100)
    private String formaFarmaceutica;
    
    @Column(nullable = false, length = 150)
    private String laboratorio;
    
    @Column(nullable = false)
    private Boolean controlado = false;
    
    @Column(nullable = false)
    private Boolean necessitaReceita = false;
    
    @Column(nullable = false)
    private Boolean generico = false;
    
    @Column(nullable = false)
    private Boolean ehSimilar = false;
    
    @Column(length = 50)
    private String tipoReceita;
    
    @Column(length = 100)
    private String codigoOrigem;
    
    @Temporal(TemporalType.DATE)
    private Date dataValidadeRegistro;
    
    @Column(length = 500)
    private String observacoes;

}