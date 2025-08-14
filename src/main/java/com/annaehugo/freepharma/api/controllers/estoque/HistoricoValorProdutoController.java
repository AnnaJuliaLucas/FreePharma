package com.annaehugo.freepharma.api.controllers.estoque;

import com.annaehugo.freepharma.application.services.HistoricoValorProdutoService;
import com.annaehugo.freepharma.domain.entity.estoque.HistoricoValorProduto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque/historico-valor-produto")
public class HistoricoValorProdutoController {

    private final HistoricoValorProdutoService historicoValorProdutoService;

    @Autowired
    public HistoricoValorProdutoController(HistoricoValorProdutoService historicoValorProdutoService) {
        this.historicoValorProdutoService = historicoValorProdutoService;
    }

    @GetMapping
    public ResponseEntity<List<HistoricoValorProduto>> listarTodos() {
        List<HistoricoValorProduto> historicos = historicoValorProdutoService.listarTodos();
        return ResponseEntity.ok(historicos);
    }

//    @GetMapping("/produto/{produtoId}")
//    public ResponseEntity<List<HistoricoValorProduto>> listarPorProduto(@PathVariable Long produtoId) {
//        List<HistoricoValorProduto> historicos = historicoValorProdutoService.listarPorProduto(produtoId);
//        return ResponseEntity.ok(historicos);
//    }


    @GetMapping("/{id}")
    public ResponseEntity<HistoricoValorProduto> buscarPorId(@PathVariable Long id) {
        return historicoValorProdutoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody HistoricoValorProduto historicoValorProduto) {
        try {
            HistoricoValorProduto historicoCreated = historicoValorProdutoService.salvar(historicoValorProduto);
            return ResponseEntity.status(HttpStatus.CREATED).body(historicoCreated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody HistoricoValorProduto historicoValorProduto) {
        try {
            HistoricoValorProduto historicoAtualizado = historicoValorProdutoService.atualizar(id, historicoValorProduto);
            return ResponseEntity.ok(historicoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            historicoValorProdutoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
