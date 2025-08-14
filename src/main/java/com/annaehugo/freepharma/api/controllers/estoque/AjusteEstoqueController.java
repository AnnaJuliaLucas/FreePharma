package com.annaehugo.freepharma.api.controllers.estoque;

import com.annaehugo.freepharma.application.dto.estoque.AjusteEstoqueProdutoDTO;
import com.annaehugo.freepharma.application.mapper.AjusteEstoqueProdutoMapper;
import com.annaehugo.freepharma.application.services.EstoqueProdutoService;
import com.annaehugo.freepharma.domain.entity.estoque.AjusteEstoqueProduto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ajustes-estoque")
public class AjusteEstoqueController {

    private final EstoqueProdutoService estoqueProdutoService;
    private final AjusteEstoqueProdutoMapper ajusteEstoqueProdutoMapper;

    @Autowired
    public AjusteEstoqueController(EstoqueProdutoService estoqueProdutoService, AjusteEstoqueProdutoMapper ajusteEstoqueProdutoMapper) {
        this.estoqueProdutoService = estoqueProdutoService;
        this.ajusteEstoqueProdutoMapper = ajusteEstoqueProdutoMapper;
    }

    @GetMapping("/produto-estoque/{produtoEstoqueId}")
    public ResponseEntity<List<AjusteEstoqueProdutoDTO>> listarAjustesPorEstoque(@PathVariable Long produtoEstoqueId) {
        List<AjusteEstoqueProduto> ajustes = estoqueProdutoService.listarAjustesPorEstoque(produtoEstoqueId);
        List<AjusteEstoqueProdutoDTO> ajustesDTO = ajusteEstoqueProdutoMapper.toDtoList(ajustes);
        return ResponseEntity.ok(ajustesDTO);
    }
}
