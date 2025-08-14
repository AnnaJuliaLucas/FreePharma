package com.annaehugo.freepharma.api.controllers.administrativo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Api(tags = "User Dashboard", description = "Operações para usuários comuns")
public class UserDashboardController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("title", "Dashboard do Usuário");
        dashboard.put("myNotifications", 5);
        dashboard.put("recentActivities", "Últimas atividades do usuário");
        dashboard.put("message", "Bem-vindo ao seu painel pessoal!");
        
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserProfile() {
        Map<String, Object> profile = new HashMap<>();
        profile.put("name", "Usuario Exemplo");
        profile.put("email", "usuario@exemplo.com");
        profile.put("role", "USER");
        profile.put("lastLogin", "2025-01-15 10:30:00");
        
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> updateProfile(@RequestBody Map<String, String> profileData) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Perfil atualizado com sucesso");
        
        return ResponseEntity.ok(response);
    }
}