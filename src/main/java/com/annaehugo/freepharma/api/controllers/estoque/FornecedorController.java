package com.annaehugo.freepharma.api.controllers.estoque;

import com.annaehugo.freepharma.application.dto.estoque.FornecedorDTO;
import com.annaehugo.freepharma.application.mapper.FornecedorMapper;
import com.annaehugo.freepharma.application.services.FornecedorService;
import com.annaehugo.freepharma.domain.entity.estoque.Fornecedor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {

    private final FornecedorService fornecedorService;
    private final FornecedorMapper fornecedorMapper;

    @Autowired
    public FornecedorController(FornecedorService fornecedorService, FornecedorMapper fornecedorMapper) {
        this.fornecedorService = fornecedorService;
        this.fornecedorMapper = fornecedorMapper;
    }

    @GetMapping
    public ResponseEntity<List<FornecedorDTO>> listarTodos() {
        List<Fornecedor> fornecedores = fornecedorService.listarTodos();
        List<FornecedorDTO> fornecedoresDTO = fornecedorMapper.toDtoList(fornecedores);
        return ResponseEntity.ok(fornecedoresDTO);
    }


    @GetMapping("/ativos")
    public ResponseEntity<List<Fornecedor>> listarAtivos() {
        List<Fornecedor> fornecedores = fornecedorService.listarAtivos();
        return ResponseEntity.ok(fornecedores);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Fornecedor>> buscarPorNome(@RequestParam String nome) {
        List<Fornecedor> fornecedores = fornecedorService.buscarPorNome(nome);
        return ResponseEntity.ok(fornecedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fornecedor> buscarPorId(@PathVariable Long id) {
        return fornecedorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<Fornecedor> buscarPorCnpj(@PathVariable String cnpj) {
        return fornecedorService.buscarPorCnpj(cnpj)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Fornecedor>> buscarPorStatus(@PathVariable String status) {
        List<Fornecedor> fornecedores = fornecedorService.buscarPorStatus(status);
        return ResponseEntity.ok(fornecedores);
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Fornecedor fornecedor) {
        try {
            Fornecedor fornecedorCreated = fornecedorService.salvar(fornecedor);
            return ResponseEntity.status(HttpStatus.CREATED).body(fornecedorCreated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Fornecedor fornecedor) {
        try {
            Fornecedor fornecedorAtualizado = fornecedorService.atualizar(id, fornecedor);
            return ResponseEntity.ok(fornecedorAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            fornecedorService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            fornecedorService.ativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        try {
            fornecedorService.inativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/bloquear")
    public ResponseEntity<?> bloquear(@PathVariable Long id) {
        try {
            fornecedorService.bloquear(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}