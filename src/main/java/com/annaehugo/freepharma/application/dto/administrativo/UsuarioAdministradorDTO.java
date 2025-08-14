package com.annaehugo.freepharma.application.dto.administrativo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAdministradorDTO {
    private Long id;
    private String nome;
    private String cpfCnpj;
//    private String email;
//    private String telefone;
    private String login;
    private Date dataCadastro;
    private String status;
    private Date ultimoAcesso;
//    private Boolean autenticacao2FA;
    private Date createdAt;
    private Date updatedAt;
    private boolean ativo;
//    private List<PerfilDTO> perfis;
//    private List<UnidadeDTO> unidadesAcesso;
}