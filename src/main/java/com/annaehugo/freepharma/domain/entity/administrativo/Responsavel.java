package com.annaehugo.freepharma.domain.entity.administrativo;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "responsavel")
public class Responsavel extends Pessoa {
    @Column(name = "registro_profissional", nullable = false)
    private String registroProfissional;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @Column(name = "crf")
    private String crf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;

}
