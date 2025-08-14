package com.annaehugo.freepharma.api.controllers.administrativo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Api(tags = "Admin Dashboard", description = "Operações exclusivas para administradores")
public class AdminDashboardController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("title", "Dashboard Administrativo");
        dashboard.put("totalUsers", 150);
        dashboard.put("totalFarmacias", 25);
        dashboard.put("systemHealth", "OK");
        dashboard.put("message", "Bem-vindo ao painel administrativo!");
        
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        response.put("users", "Lista de todos os usuários do sistema");
        response.put("count", 150);
        response.put("adminOnly", true);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/system/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> updateSystemConfig(@RequestBody Map<String, String> config) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Configurações atualizadas com sucesso");
        
        return ResponseEntity.ok(response);
    }
}