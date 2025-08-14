package com.annaehugo.freepharma.application.dto.administrativo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAdministradorResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String login;
    private Date dataCadastro;
    private String status;
    private Date ultimoAcesso;
    private Boolean autenticacao2FA;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    private List<PerfilDTO> perfis;
    private List<UnidadeDTO> unidadesAcesso;
    
    public String getCpfCnpjMascarado() {
        return maskCpfCnpj(this.cpfCnpj);
    }
    
    private String cpfCnpj;
    
    private String maskCpfCnpj(String cpfCnpj) {
        if (cpfCnpj == null || cpfCnpj.length() < 8) return "***";
        if (cpfCnpj.length() == 11) { // CPF
            return cpfCnpj.substring(0, 3) + ".***.***-" + cpfCnpj.substring(9);
        } else if (cpfCnpj.length() == 14) { // CNPJ
            return cpfCnpj.substring(0, 2) + ".***.***/**" + cpfCnpj.substring(12);
        }
        return "***";
    }
}