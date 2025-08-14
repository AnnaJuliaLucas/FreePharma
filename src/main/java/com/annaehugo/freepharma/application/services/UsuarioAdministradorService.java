package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador;
import com.annaehugo.freepharma.domain.entity.administrativo.Perfil;
import com.annaehugo.freepharma.domain.repository.administrativo.UsuarioAdministradorRepository;
import com.annaehugo.freepharma.domain.repository.administrativo.PerfilRepository;
import com.annaehugo.freepharma.domain.repository.administrativo.UnidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioAdministradorService {

    private final UsuarioAdministradorRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final UnidadeRepository unidadeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioAdministradorService(
            UsuarioAdministradorRepository usuarioRepository,
            PerfilRepository perfilRepository,
            UnidadeRepository unidadeRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.unidadeRepository = unidadeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioAdministrador> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<UsuarioAdministrador> listarPorStatus(String status) {
        return usuarioRepository.findByStatus(status);
    }

    public List<UsuarioAdministrador> listarPorUnidade(Long unidadeId) {
        return usuarioRepository.findByUnidadesAcessoId(unidadeId);
    }

    public Optional<UsuarioAdministrador> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<UsuarioAdministrador> buscarPorLogin(String login) {
        return usuarioRepository.findByLogin(login);
    }

    public Optional<UsuarioAdministrador> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public UsuarioAdministrador salvar(UsuarioAdministrador usuario) {
        validarDadosUsuario(usuario);
        
        // Verificar se login já existe
        if (usuario.getId() == null) {
            Optional<UsuarioAdministrador> existente = usuarioRepository.findByLogin(usuario.getLogin());
            if (existente.isPresent()) {
                throw new RuntimeException("Login já existe");
            }
        }
        
        // Verificar se email já existe
        if (usuario.getEmail() != null) {
            Optional<UsuarioAdministrador> existenteEmail = usuarioRepository.findByEmail(usuario.getEmail());
            if (existenteEmail.isPresent() && !existenteEmail.get().getId().equals(usuario.getId())) {
                throw new RuntimeException("Email já cadastrado");
            }
        }
        
        // Criptografar senha se for novo usuário ou se senha foi alterada
        if (usuario.getId() == null || usuario.getSenha() != null) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        
        // Set data de cadastro para novos usuários
        if (usuario.getId() == null) {
            usuario.setDataCadastro(new Date());
            usuario.setStatus("ATIVO");
        }
        
        return usuarioRepository.save(usuario);
    }

    public UsuarioAdministrador atualizar(Long id, UsuarioAdministrador usuarioAtualizado) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuarioAtualizado.setId(id);
                    // Não permitir alterar login e data de cadastro
                    usuarioAtualizado.setLogin(usuario.getLogin());
                    usuarioAtualizado.setDataCadastro(usuario.getDataCadastro());
                    
                    // Se senha não foi informada, manter a atual
                    if (usuarioAtualizado.getSenha() == null || usuarioAtualizado.getSenha().trim().isEmpty()) {
                        usuarioAtualizado.setSenha(usuario.getSenha());
                    } else {
                        usuarioAtualizado.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
                    }
                    
                    validarDadosUsuario(usuarioAtualizado);
                    return usuarioRepository.save(usuarioAtualizado);
                })
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    public void ativar(Long id) {
        usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setStatus("ATIVO");
                    return usuarioRepository.save(usuario);
                })
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void inativar(Long id) {
        usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setStatus("INATIVO");
                    return usuarioRepository.save(usuario);
                })
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void bloquear(Long id) {
        usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setStatus("BLOQUEADO");
                    return usuarioRepository.save(usuario);
                })
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void alterarSenha(Long id, String senhaAtual, String novaSenha) {
        UsuarioAdministrador usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new RuntimeException("Senha atual incorreta");
        }
        
        if (novaSenha == null || novaSenha.length() < 6) {
            throw new RuntimeException("Nova senha deve ter pelo menos 6 caracteres");
        }
        
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    public void atribuirPerfis(Long usuarioId, List<Long> perfilIds) {
        UsuarioAdministrador usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        List<Perfil> perfis = perfilRepository.findAllById(perfilIds);
        usuario.setPerfis(perfis);
        usuarioRepository.save(usuario);
    }

    public void registrarAcesso(Long id) {
        usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setUltimoAcesso(new Date());
                    return usuarioRepository.save(usuario);
                })
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    private void validarDadosUsuario(UsuarioAdministrador usuario) {
        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            throw new RuntimeException("Login é obrigatório");
        }
        
        if (usuario.getLogin().length() < 3) {
            throw new RuntimeException("Login deve ter pelo menos 3 caracteres");
        }
        
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome é obrigatório");
        }
        
        if (usuario.getEmail() != null && !usuario.getEmail().trim().isEmpty()) {
            if (!isValidEmail(usuario.getEmail())) {
                throw new RuntimeException("Email inválido");
            }
        }
        
        // Validar se todas as unidades de acesso existem
        if (usuario.getUnidadesAcesso() != null && !usuario.getUnidadesAcesso().isEmpty()) {
            for (var unidade : usuario.getUnidadesAcesso()) {
                if (unidade.getId() != null && !unidadeRepository.existsById(unidade.getId())) {
                    throw new RuntimeException("Unidade não encontrada: " + unidade.getId());
                }
            }
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}