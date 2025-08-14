package com.annaehugo.freepharma.api.controllers.debug;

import com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador;
import com.annaehugo.freepharma.domain.repository.administrativo.UsuarioAdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    private UsuarioAdministradorRepository usuarioRepository;

    @GetMapping("/user-roles")
    public ResponseEntity<Map<String, Object>> getUserRoles(@RequestParam String email) {
        Map<String, Object> debug = new HashMap<>();
        
        try {
            UsuarioAdministrador usuario = usuarioRepository.findByEmail(email).orElse(null);
            
            if (usuario == null) {
                debug.put("error", "Usuario não encontrado");
                return ResponseEntity.ok(debug);
            }
            
            debug.put("userId", usuario.getId());
            debug.put("userName", usuario.getNome());
            debug.put("email", usuario.getEmail());
            debug.put("perfisCount", usuario.getPerfis() != null ? usuario.getPerfis().size() : 0);
            
            if (usuario.getPerfis() != null) {
                debug.put("perfis", usuario.getPerfis().stream()
                    .map(p -> Map.of("id", p.getId(), "nome", p.getNome(), "ativo", p.getAtivo()))
                    .collect(Collectors.toList()));
            }
            
        } catch (Exception e) {
            debug.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(debug);
    }

    @GetMapping("/current-auth")
    public ResponseEntity<Map<String, Object>> getCurrentAuth() {
        Map<String, Object> debug = new HashMap<>();
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            debug.put("principal", auth.getPrincipal().toString());
            debug.put("authorities", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
            debug.put("authenticated", auth.isAuthenticated());
        } else {
            debug.put("error", "No authentication found");
        }
        
        return ResponseEntity.ok(debug);
    }
}