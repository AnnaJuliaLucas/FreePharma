package com.annaehugo.freepharma.api.controllers.estoque;

import com.annaehugo.freepharma.application.dto.estoque.ProdutoReferenciaDTO;
import com.annaehugo.freepharma.application.mapper.ProdutoReferenciaMapper;
import com.annaehugo.freepharma.application.services.ProdutoReferenciaService;
import com.annaehugo.freepharma.domain.entity.estoque.ProdutoReferencia;
import com.annaehugo.freepharma.domain.entity.estoque.Medicamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoReferenciaController {

    private final ProdutoReferenciaService produtoService;
    private final ProdutoReferenciaMapper produtoReferenciaMapper;

    @Autowired
    public ProdutoReferenciaController(ProdutoReferenciaService produtoService, ProdutoReferenciaMapper produtoReferenciaMapper) {
        this.produtoService = produtoService;
        this.produtoReferenciaMapper = produtoReferenciaMapper;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoReferenciaDTO>> listarTodos() {
        List<ProdutoReferencia> produtos = produtoService.listarTodos();
        List<ProdutoReferenciaDTO> produtosDTO = produtoReferenciaMapper.toDtoList(produtos);
        return ResponseEntity.ok(produtosDTO);
    }


    @GetMapping("/ativos")
    public ResponseEntity<List<ProdutoReferenciaDTO>> listarAtivos() {
        List<ProdutoReferencia> produtos = produtoService.listarAtivos();
        List<ProdutoReferenciaDTO> produtosDTO = produtoReferenciaMapper.toDtoList(produtos);
        return ResponseEntity.ok(produtosDTO);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoReferenciaDTO>> buscarPorNome(@RequestParam String nome) {
        List<ProdutoReferencia> produtos = produtoService.buscarPorNome(nome);
        List<ProdutoReferenciaDTO> produtosDTO = produtoReferenciaMapper.toDtoList(produtos);
        return ResponseEntity.ok(produtosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoReferenciaDTO> buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(produtoReferenciaMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigoInterno}")
    public ResponseEntity<ProdutoReferenciaDTO> buscarPorCodigoInterno(@PathVariable String codigoInterno) {
        return produtoService.buscarPorCodigoInterno(codigoInterno)
                .map(produtoReferenciaMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ean/{ean}")
    public ResponseEntity<ProdutoReferenciaDTO> buscarPorEan(@PathVariable String ean) {
        return produtoService.buscarPorEan(ean)
                .map(produtoReferenciaMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProdutoReferenciaDTO>> buscarPorStatus(@PathVariable String status) {
        List<ProdutoReferencia> produtos = produtoService.buscarPorStatus(status);
        List<ProdutoReferenciaDTO> produtosDTO = produtoReferenciaMapper.toDtoList(produtos);
        return ResponseEntity.ok(produtosDTO);
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody ProdutoReferenciaDTO produtoDTO) {
        try {
            System.out.println("Recebendo produto para criar: " + produtoDTO);
            ProdutoReferencia produto = produtoReferenciaMapper.toEntity(produtoDTO);
            System.out.println("Produto mapeado: " + produto);
            ProdutoReferencia produtoCreated = produtoService.salvar(produto);
            ProdutoReferenciaDTO produtoCreatedDTO = produtoReferenciaMapper.toDto(produtoCreated);
            return ResponseEntity.status(HttpStatus.CREATED).body(produtoCreatedDTO);
        } catch (Exception e) {
            System.err.println("Erro ao criar produto: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao criar produto: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody ProdutoReferenciaDTO produtoDTO) {
        try {
            ProdutoReferencia produto = produtoReferenciaMapper.toEntity(produtoDTO);
            ProdutoReferencia produtoAtualizado = produtoService.atualizar(id, produto);
            ProdutoReferenciaDTO produtoAtualizadoDTO = produtoReferenciaMapper.toDto(produtoAtualizado);
            return ResponseEntity.ok(produtoAtualizadoDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            produtoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            produtoService.ativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        try {
            produtoService.inativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PatchMapping("/{id}/descontinuar")
//    public ResponseEntity<?> descontinuar(@PathVariable Long id) {
//        try {
//            produtoService.descontinuar(id);
//            return ResponseEntity.ok().build();
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @GetMapping("/medicamentos")
    public ResponseEntity<List<Medicamento>> listarMedicamentos() {
        List<Medicamento> medicamentos = produtoService.listarMedicamentos();
        return ResponseEntity.ok(medicamentos);
    }

    @GetMapping("/medicamentos/principio-ativo")
    public ResponseEntity<List<Medicamento>> buscarMedicamentosPorPrincipioAtivo(@RequestParam String principioAtivo) {
        List<Medicamento> medicamentos = produtoService.buscarMedicamentosPorPrincipioAtivo(principioAtivo);
        return ResponseEntity.ok(medicamentos);
    }

    @GetMapping("/medicamentos/controlados")
    public ResponseEntity<List<Medicamento>> listarMedicamentosControlados() {
        List<Medicamento> medicamentos = produtoService.listarMedicamentosControlados();
        return ResponseEntity.ok(medicamentos);
    }

    @GetMapping("/medicamentos/genericos")
    public ResponseEntity<List<Medicamento>> listarMedicamentosGenericos() {
        List<Medicamento> medicamentos = produtoService.listarMedicamentosGenericos();
        return ResponseEntity.ok(medicamentos);
    }

    @GetMapping("/medicamentos/anvisa/{registroAnvisa}")
    public ResponseEntity<Medicamento> buscarMedicamentoPorRegistroAnvisa(@PathVariable String registroAnvisa) {
        return produtoService.buscarMedicamentoPorRegistroAnvisa(registroAnvisa)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
