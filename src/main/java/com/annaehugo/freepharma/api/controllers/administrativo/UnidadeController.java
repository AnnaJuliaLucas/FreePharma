package com.annaehugo.freepharma.api.controllers.administrativo;

import com.annaehugo.freepharma.application.dto.administrativo.UnidadeDTO;
import com.annaehugo.freepharma.application.mapper.UnidadeMapper;
import com.annaehugo.freepharma.application.services.UnidadeService;
import com.annaehugo.freepharma.domain.entity.administrativo.Unidade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
public class UnidadeController {

    private final UnidadeService unidadeService;
    private final UnidadeMapper unidadeMapper;

    @Autowired
    public UnidadeController(UnidadeService unidadeService, UnidadeMapper unidadeMapper) {
        this.unidadeService = unidadeService;
        this.unidadeMapper = unidadeMapper;
    }

    @GetMapping
    public ResponseEntity<List<UnidadeDTO>> listarTodas() {
        List<Unidade> unidades = unidadeService.listarTodas();
        List<UnidadeDTO> unidadesDTO = unidadeMapper.toDtoList(unidades);
        return ResponseEntity.ok(unidadesDTO);
    }

    @GetMapping("/farmacia/{farmaciaId}")
    public ResponseEntity<List<UnidadeDTO>> listarPorFarmacia(@PathVariable Long farmaciaId) {
        List<Unidade> unidades = unidadeService.listarPorFarmacia(farmaciaId);
        List<UnidadeDTO> unidadesDTO = unidadeMapper.toDtoList(unidades);
        return ResponseEntity.ok(unidadesDTO);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UnidadeDTO> buscarPorId(@PathVariable Long id) {
        return unidadeService.buscarPorId(id)
                .map(unidadeMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<Unidade> buscarPorCnpj(@PathVariable String cnpj) {
        return unidadeService.buscarPorCnpj(cnpj)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Unidade>> buscarPorStatus(@PathVariable String status) {
        List<Unidade> unidades = unidadeService.buscarPorStatus(status);
        return ResponseEntity.ok(unidades);
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody UnidadeDTO unidadeDTO) {
        try {
            Unidade unidade = unidadeMapper.toEntity(unidadeDTO);
            Unidade unidadeCreated = unidadeService.salvar(unidade);
            UnidadeDTO unidadeCreatedDTO = unidadeMapper.toDto(unidadeCreated);
            return ResponseEntity.status(HttpStatus.CREATED).body(unidadeCreatedDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody UnidadeDTO unidadeDTO) {
        try {
            Unidade unidade = unidadeMapper.toEntity(unidadeDTO);
            Unidade unidadeAtualizada = unidadeService.atualizar(id, unidade);
            UnidadeDTO unidadeAtualizadaDTO = unidadeMapper.toDto(unidadeAtualizada);
            return ResponseEntity.ok(unidadeAtualizadaDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            unidadeService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            unidadeService.ativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        try {
            unidadeService.inativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
