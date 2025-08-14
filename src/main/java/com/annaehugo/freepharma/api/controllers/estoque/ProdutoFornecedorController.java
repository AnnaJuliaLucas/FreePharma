package com.annaehugo.freepharma.api.controllers.estoque;

import com.annaehugo.freepharma.application.services.FornecedorService;
import com.annaehugo.freepharma.domain.entity.estoque.ProdutoFornecedor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque/produto-fornecedor")
public class ProdutoFornecedorController {

    private final FornecedorService fornecedorService;

    @Autowired
    public ProdutoFornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

//    @GetMapping("/fornecedor/{fornecedorId}")
//    public ResponseEntity<List<ProdutoFornecedor>> listarProdutosPorFornecedor(@PathVariable Long fornecedorId) {
//        List<ProdutoFornecedor> produtos = fornecedorService.listarProdutosPorFornecedor(fornecedorId);
//        return ResponseEntity.ok(produtos);
//    }
}
