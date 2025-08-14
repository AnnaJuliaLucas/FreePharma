package com.annaehugo.freepharma.api.controllers.administrativo;

import com.annaehugo.freepharma.application.dto.administrativo.UsuarioAdministradorDTO;
import com.annaehugo.freepharma.application.dto.administrativo.UsuarioAdministradorResponseDTO;
import com.annaehugo.freepharma.application.dto.administrativo.UsuarioAdministradorCreateDTO;
import com.annaehugo.freepharma.application.mapper.UsuarioAdministradorMapper;
import com.annaehugo.freepharma.application.services.UsuarioAdministradorService;
import com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios-administradores")
public class UsuarioAdministradorController {

    private final UsuarioAdministradorService usuarioService;
    private final UsuarioAdministradorMapper usuarioMapper;

    @Autowired
    public UsuarioAdministradorController(UsuarioAdministradorService usuarioService, UsuarioAdministradorMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioAdministradorResponseDTO>> listarTodos() {
        List<UsuarioAdministrador> usuarios = usuarioService.listarTodos();
        List<UsuarioAdministradorResponseDTO> usuariosDTO = usuarios.stream()
                .map(usuarioMapper::toResponseDto)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(usuariosDTO);
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<List<UsuarioAdministradorDTO>> listarPorStatus(@PathVariable String status) {
        List<UsuarioAdministrador> usuarios = usuarioService.listarPorStatus(status);
        List<UsuarioAdministradorDTO> usuariosDTO = usuarioMapper.toDtoList(usuarios);
        return ResponseEntity.ok(usuariosDTO);
    }

    @GetMapping("/unidade/{unidadeId}")
    public ResponseEntity<List<UsuarioAdministradorDTO>> listarPorUnidade(@PathVariable Long unidadeId) {
        List<UsuarioAdministrador> usuarios = usuarioService.listarPorUnidade(unidadeId);
        List<UsuarioAdministradorDTO> usuariosDTO = usuarioMapper.toDtoList(usuarios);
        return ResponseEntity.ok(usuariosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioAdministradorDTO> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(usuarioMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/login/{login}")
    public ResponseEntity<UsuarioAdministradorDTO> buscarPorLogin(@PathVariable String login) {
        return usuarioService.buscarPorLogin(login)
                .map(usuarioMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioAdministradorDTO> buscarPorEmail(@PathVariable String email) {
        return usuarioService.buscarPorEmail(email)
                .map(usuarioMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody UsuarioAdministradorDTO usuarioDTO) {
        try {
            UsuarioAdministrador usuario = usuarioMapper.toEntity(usuarioDTO);
            UsuarioAdministrador usuarioCreated = usuarioService.salvar(usuario);
            UsuarioAdministradorDTO usuarioCreatedDTO = usuarioMapper.toDto(usuarioCreated);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreatedDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody UsuarioAdministradorDTO usuarioDTO) {
        try {
            UsuarioAdministrador usuario = usuarioMapper.toEntity(usuarioDTO);
            UsuarioAdministrador usuarioAtualizado = usuarioService.atualizar(id, usuario);
            UsuarioAdministradorDTO usuarioAtualizadoDTO = usuarioMapper.toDto(usuarioAtualizado);
            return ResponseEntity.ok(usuarioAtualizadoDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            usuarioService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            usuarioService.ativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        try {
            usuarioService.inativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/bloquear")
    public ResponseEntity<?> bloquear(@PathVariable Long id) {
        try {
            usuarioService.bloquear(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/alterar-senha")
    public ResponseEntity<?> alterarSenha(@PathVariable Long id, @RequestBody Map<String, String> senhas) {
        try {
            String senhaAtual = senhas.get("senhaAtual");
            String novaSenha = senhas.get("novaSenha");
            usuarioService.alterarSenha(id, senhaAtual, novaSenha);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/perfis")
    public ResponseEntity<?> atribuirPerfis(@PathVariable Long id, @RequestBody List<Long> perfilIds) {
        try {
            usuarioService.atribuirPerfis(id, perfilIds);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/registrar-acesso")
    public ResponseEntity<?> registrarAcesso(@PathVariable Long id) {
        try {
            usuarioService.registrarAcesso(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
