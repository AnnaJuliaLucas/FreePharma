package com.annaehugo.freepharma.api.controllers.fiscal;

import com.annaehugo.freepharma.application.dto.fiscal.LoteProcessamentoDTO;
import com.annaehugo.freepharma.application.mapper.LoteProcessamentoMapper;
import com.annaehugo.freepharma.application.services.LoteProcessamentoService;
import com.annaehugo.freepharma.domain.entity.fiscal.LoteProcessamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fiscal/lote-processamento")
public class LoteProcessamentoController {

    private final LoteProcessamentoService loteProcessamentoService;
    private final LoteProcessamentoMapper loteProcessamentoMapper;

    @Autowired
    public LoteProcessamentoController(LoteProcessamentoService loteProcessamentoService, LoteProcessamentoMapper loteProcessamentoMapper) {
        this.loteProcessamentoService = loteProcessamentoService;
        this.loteProcessamentoMapper = loteProcessamentoMapper;
    }

    @GetMapping
    public ResponseEntity<List<LoteProcessamentoDTO>> listarTodos() {
        List<LoteProcessamento> lotes = loteProcessamentoService.listarTodos();
        List<LoteProcessamentoDTO> lotesDTO = loteProcessamentoMapper.toDtoList(lotes);
        return ResponseEntity.ok(lotesDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteProcessamentoDTO> buscarPorId(@PathVariable Long id) {
        return loteProcessamentoService.buscarPorId(id)
                .map(loteProcessamentoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoteProcessamentoDTO>> listarPorStatus(@PathVariable String status) {
        List<LoteProcessamento> lotes = loteProcessamentoService.listarPorStatus(status);
        List<LoteProcessamentoDTO> lotesDTO = loteProcessamentoMapper.toDtoList(lotes);
        return ResponseEntity.ok(lotesDTO);
    }

    @PostMapping
    public ResponseEntity<LoteProcessamentoDTO> criar(@RequestBody LoteProcessamentoDTO loteProcessamentoDTO) {
        LoteProcessamento loteProcessamento = loteProcessamentoMapper.toEntity(loteProcessamentoDTO);
        LoteProcessamento loteCreated = loteProcessamentoService.salvar(loteProcessamento);
        LoteProcessamentoDTO loteCreatedDTO = loteProcessamentoMapper.toDto(loteCreated);
        return ResponseEntity.ok(loteCreatedDTO);
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<LoteProcessamentoDTO> finalizar(@PathVariable Long id, @RequestParam String status) {
        LoteProcessamento loteFinalizado = loteProcessamentoService.finalizar(id, status);
        LoteProcessamentoDTO loteFinalizadoDTO = loteProcessamentoMapper.toDto(loteFinalizado);
        return ResponseEntity.ok(loteFinalizadoDTO);
    }
}
