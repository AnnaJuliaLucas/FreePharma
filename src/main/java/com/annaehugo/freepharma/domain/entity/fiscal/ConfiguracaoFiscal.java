package com.annaehugo.freepharma.domain.entity.fiscal;

import com.annaehugo.freepharma.domain.entity.base.EntidadeBase;
import javax.persistence.Entity;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoFiscal extends EntidadeBase {
    private String regimeTributario;

}
