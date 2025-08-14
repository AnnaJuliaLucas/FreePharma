package com.annaehugo.freepharma.application.dto.administrativo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Long id;
    private String nome;
    private String cpfCnpj;
    private String endereco;
    private String email;
    private String telefone;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
    
    public String getCpfCnpjMascarado() {
        return maskCpfCnpj(this.cpfCnpj);
    }
    
    public String getEmailMascarado() {
        return maskEmail(this.email);
    }
    
    private String maskCpfCnpj(String cpfCnpj) {
        if (cpfCnpj == null || cpfCnpj.length() < 8) return "***";
        if (cpfCnpj.length() == 11) { // CPF
            return cpfCnpj.substring(0, 3) + ".***.***-" + cpfCnpj.substring(9);
        } else if (cpfCnpj.length() == 14) { // CNPJ
            return cpfCnpj.substring(0, 2) + ".***.***/**" + cpfCnpj.substring(12);
        }
        return "***";
    }
    
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***@***.***";
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];
        
        if (localPart.length() <= 2) return "***@" + domain;
        return localPart.substring(0, 2) + "***@" + domain;
    }
}