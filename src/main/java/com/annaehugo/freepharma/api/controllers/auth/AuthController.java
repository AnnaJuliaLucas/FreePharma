package com.annaehugo.freepharma.api.controllers.auth;

import com.annaehugo.freepharma.application.mapper.UsuarioAdministradorMapper;
import com.annaehugo.freepharma.application.services.AuthenticationService;
import com.annaehugo.freepharma.application.services.AuthenticationService.AuthenticationRequest;
import com.annaehugo.freepharma.application.services.AuthenticationService.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UsuarioAdministradorMapper usuarioAdministradorMapper;

    @Autowired
    public AuthController(AuthenticationService authenticationService , UsuarioAdministradorMapper usuarioAdministradorMapper) {
        this.authenticationService = authenticationService;
        this.usuarioAdministradorMapper = usuarioAdministradorMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse response = authenticationService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        try {
            String refreshToken = refreshTokenRequest.get("refreshToken");
            if (refreshToken == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Refresh token é obrigatório"));
            }
            
            AuthenticationResponse response = authenticationService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logout realizado com sucesso"));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        return ResponseEntity.ok(Map.of("valid", true, "message", "Token válido"));
    }
}