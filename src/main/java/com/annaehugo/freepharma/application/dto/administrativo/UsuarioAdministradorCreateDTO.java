package com.annaehugo.freepharma.application.dto.administrativo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAdministradorCreateDTO {
    private String nome;
    private String cpfCnpj;
    private String email;
    private String telefone;
    private String login;
    private String senha;
    private Boolean autenticacao2FA;
    private List<Long> perfilIds;
    private List<Long> unidadeAcessoIds;
}