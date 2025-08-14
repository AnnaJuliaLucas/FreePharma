package com.annaehugo.freepharma.api.controllers.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller temporário para gerar hashes de senha para testes
 * REMOVER EM PRODUÇÃO!
 */
@RestController
@RequestMapping("/api/util")
public class PasswordUtilController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/generate-hash")
    public ResponseEntity<?> generateHash(@RequestBody Map<String, String> request) {
        String senha = request.get("senha");
        if (senha == null) {
            return ResponseEntity.badRequest().body("Campo 'senha' é obrigatório");
        }

        String hash = passwordEncoder.encode(senha);
        boolean matches = passwordEncoder.matches(senha, hash);

        return ResponseEntity.ok(Map.of(
            "senhaOriginal", senha,
            "hashGerado", hash,
            "validacao", matches,
            "tamanho", hash.length()
        ));
    }

    @PostMapping("/validate-hash")
    public ResponseEntity<?> validateHash(@RequestBody Map<String, String> request) {
        String senha = request.get("senha");
        String hash = request.get("hash");
        
        if (senha == null || hash == null) {
            return ResponseEntity.badRequest().body("Campos 'senha' e 'hash' são obrigatórios");
        }

        boolean matches = passwordEncoder.matches(senha, hash);

        return ResponseEntity.ok(Map.of(
            "senha", senha,
            "hash", hash,
            "matches", matches
        ));
    }
}