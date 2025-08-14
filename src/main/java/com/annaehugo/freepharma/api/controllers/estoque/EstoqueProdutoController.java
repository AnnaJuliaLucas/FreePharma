package com.annaehugo.freepharma.api.controllers.estoque;

import com.annaehugo.freepharma.application.services.EstoqueProdutoService;
import com.annaehugo.freepharma.domain.entity.estoque.EstoqueProduto;
import com.annaehugo.freepharma.domain.entity.estoque.AjusteEstoqueProduto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueProdutoController {

    private final EstoqueProdutoService estoqueProdutoService;

    @Autowired
    public EstoqueProdutoController(EstoqueProdutoService estoqueProdutoService) {
        this.estoqueProdutoService = estoqueProdutoService;
    }

    @GetMapping
    public ResponseEntity<List<EstoqueProduto>> listarTodos() {
        List<EstoqueProduto> estoques = estoqueProdutoService.listarTodos();
        return ResponseEntity.ok(estoques);
    }

    @GetMapping("/unidade/{unidadeId}")
    public ResponseEntity<List<EstoqueProduto>> listarPorUnidade(@PathVariable Long unidadeId) {
        List<EstoqueProduto> estoques = estoqueProdutoService.listarPorUnidade(unidadeId);
        return ResponseEntity.ok(estoques);
    }

    @GetMapping("/produto/{produtoId}/unidade/{unidadeId}")
    public ResponseEntity<List<EstoqueProduto>> listarPorProduto(
            @PathVariable Long produtoId, 
            @PathVariable Long unidadeId) {
        List<EstoqueProduto> estoques = estoqueProdutoService.listarPorProduto(produtoId, unidadeId);
        return ResponseEntity.ok(estoques);
    }

//    @GetMapping("/baixo")
//    public ResponseEntity<List<EstoqueProduto>> listarEstoqueBaixo() {
//        List<EstoqueProduto> estoques = estoqueProdutoService.listarEstoqueBaixo();
//        return ResponseEntity.ok(estoques);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueProduto> buscarPorId(@PathVariable Long id) {
        return estoqueProdutoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/produto/{produtoId}/unidade/{unidadeId}/lote/{lote}")
    public ResponseEntity<EstoqueProduto> buscarPorProdutoUnidadeLote(
            @PathVariable Long produtoId,
            @PathVariable Long unidadeId, 
            @PathVariable String lote) {
        return estoqueProdutoService.buscarPorProdutoUnidadeLote(produtoId, unidadeId, lote)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody EstoqueProduto produtoEstoque) {
        try {
            EstoqueProduto estoqueCreated = estoqueProdutoService.salvar(produtoEstoque);
            return ResponseEntity.status(HttpStatus.CREATED).body(estoqueCreated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody EstoqueProduto produtoEstoque) {
        try {
            EstoqueProduto estoqueAtualizado = estoqueProdutoService.atualizar(id, produtoEstoque);
            return ResponseEntity.ok(estoqueAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            estoqueProdutoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/ajustar")
    public ResponseEntity<?> ajustarEstoque(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> ajuste) {
        try {
            Integer novaQuantidade = (Integer) ajuste.get("novaQuantidade");
            String motivo = (String) ajuste.get("motivo");
            Long usuarioId = Long.valueOf(ajuste.get("usuarioId").toString());
            
            EstoqueProduto estoqueAjustado = estoqueProdutoService.ajustarEstoque(id, novaQuantidade, motivo, usuarioId);
            return ResponseEntity.ok(estoqueAjustado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PatchMapping("/{id}/bloquear")
    public ResponseEntity<?> bloquearEstoque(@PathVariable Long id, @RequestBody Map<String, String> dados) {
        try {
            String motivo = dados.get("motivo");
            EstoqueProduto estoqueBloqueado = estoqueProdutoService.bloquearEstoque(id, motivo);
            return ResponseEntity.ok(estoqueBloqueado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/desbloquear")
    public ResponseEntity<?> desbloquearEstoque(@PathVariable Long id) {
        try {
            EstoqueProduto estoqueDesbloqueado = estoqueProdutoService.desbloquearEstoque(id);
            return ResponseEntity.ok(estoqueDesbloqueado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/ajustes")
    public ResponseEntity<List<AjusteEstoqueProduto>> listarAjustesPorEstoque(@PathVariable Long id) {
        List<AjusteEstoqueProduto> ajustes = estoqueProdutoService.listarAjustesPorEstoque(id);
        return ResponseEntity.ok(ajustes);
    }
}
