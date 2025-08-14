package com.annaehugo.freepharma.api.controllers.fiscal;

import com.annaehugo.freepharma.application.dto.fiscal.InconsistenciaDTO;
import com.annaehugo.freepharma.application.mapper.InconsistenciaMapper;
import com.annaehugo.freepharma.application.services.InconsistenciaService;
import com.annaehugo.freepharma.domain.entity.fiscal.Inconsistencia;
import com.annaehugo.freepharma.domain.entity.fiscal.TipoInconsistencia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fiscal/inconsistencias")
public class InconsistenciaController {

    private final InconsistenciaService inconsistenciaService;
    private final InconsistenciaMapper inconsistenciaMapper;

    @Autowired
    public InconsistenciaController(InconsistenciaService inconsistenciaService, InconsistenciaMapper inconsistenciaMapper) {
        this.inconsistenciaService = inconsistenciaService;
        this.inconsistenciaMapper = inconsistenciaMapper;
    }

    @GetMapping
    public ResponseEntity<List<InconsistenciaDTO>> listarTodas() {
        List<Inconsistencia> inconsistencias = inconsistenciaService.listarTodas();
        List<InconsistenciaDTO> inconsistenciasDTO = inconsistenciaMapper.toDtoList(inconsistencias);
        return ResponseEntity.ok(inconsistenciasDTO);
    }

    @GetMapping("/nota-fiscal/{notaFiscalId}")
    public ResponseEntity<List<InconsistenciaDTO>> listarPorNotaFiscal(@PathVariable Long notaFiscalId) {
        List<Inconsistencia> inconsistencias = inconsistenciaService.listarPorNotaFiscal(notaFiscalId);
        List<InconsistenciaDTO> inconsistenciasDTO = inconsistenciaMapper.toDtoList(inconsistencias);
        return ResponseEntity.ok(inconsistenciasDTO);
    }


    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<InconsistenciaDTO>> listarPorTipo(@PathVariable String tipo) {
        List<Inconsistencia> inconsistencias = inconsistenciaService.listarPorTipo(TipoInconsistencia.valueOf(tipo));
        List<InconsistenciaDTO> inconsistenciasDTO = inconsistenciaMapper.toDtoList(inconsistencias);
        return ResponseEntity.ok(inconsistenciasDTO);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InconsistenciaDTO>> listarPorStatus(@PathVariable String status) {
        List<Inconsistencia> inconsistencias = inconsistenciaService.listarPorStatus(status);
        List<InconsistenciaDTO> inconsistenciasDTO = inconsistenciaMapper.toDtoList(inconsistencias);
        return ResponseEntity.ok(inconsistenciasDTO);
    }

    @GetMapping("/nao-resolvidas")
    public ResponseEntity<List<InconsistenciaDTO>> listarNaoResolvidas() {
        List<Inconsistencia> inconsistencias = inconsistenciaService.listarNaoResolvidas();
        List<InconsistenciaDTO> inconsistenciasDTO = inconsistenciaMapper.toDtoList(inconsistencias);
        return ResponseEntity.ok(inconsistenciasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InconsistenciaDTO> buscarPorId(@PathVariable Long id) {
        return inconsistenciaService.buscarPorId(id)
                .map(inconsistenciaMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody InconsistenciaDTO inconsistenciaDTO) {
        try {
            Inconsistencia inconsistencia = inconsistenciaMapper.toEntity(inconsistenciaDTO);
            Inconsistencia inconsistenciaCreated = inconsistenciaService.salvar(inconsistencia);
            InconsistenciaDTO inconsistenciaCreatedDTO = inconsistenciaMapper.toDto(inconsistenciaCreated);
            return ResponseEntity.status(HttpStatus.CREATED).body(inconsistenciaCreatedDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody InconsistenciaDTO inconsistenciaDTO) {
        try {
            Inconsistencia inconsistencia = inconsistenciaMapper.toEntity(inconsistenciaDTO);
            Inconsistencia inconsistenciaAtualizada = inconsistenciaService.atualizar(id, inconsistencia);
            InconsistenciaDTO inconsistenciaAtualizadaDTO = inconsistenciaMapper.toDto(inconsistenciaAtualizada);
            return ResponseEntity.ok(inconsistenciaAtualizadaDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            inconsistenciaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/resolver")
    public ResponseEntity<?> resolver(@PathVariable Long id, @RequestBody Map<String, String> dados) {
        try {
            String observacao = dados.get("observacao");
            Inconsistencia inconsistenciaResolvida = inconsistenciaService.resolver(id, observacao);
            InconsistenciaDTO inconsistenciaResolvidaDTO = inconsistenciaMapper.toDto(inconsistenciaResolvida);
            return ResponseEntity.ok(inconsistenciaResolvidaDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/reabrir")
    public ResponseEntity<?> reabrir(@PathVariable Long id, @RequestBody Map<String, String> dados) {
        try {
            String motivo = dados.get("motivo");
            Inconsistencia inconsistenciaReaberta = inconsistenciaService.reabrir(id, motivo);
            InconsistenciaDTO inconsistenciaReabertaDTO = inconsistenciaMapper.toDto(inconsistenciaReaberta);
            return ResponseEntity.ok(inconsistenciaReabertaDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
