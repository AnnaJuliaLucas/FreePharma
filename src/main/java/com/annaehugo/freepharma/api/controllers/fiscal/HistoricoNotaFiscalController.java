package com.annaehugo.freepharma.api.controllers.fiscal;

import com.annaehugo.freepharma.application.dto.fiscal.HistoricoNotaFiscalDTO;
import com.annaehugo.freepharma.application.mapper.HistoricoNotaFiscalMapper;
import com.annaehugo.freepharma.application.services.HistoricoNotaFiscalService;
import com.annaehugo.freepharma.domain.entity.fiscal.HistoricoNotaFiscal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fiscal/historico-nota-fiscal")
public class HistoricoNotaFiscalController {

    private final HistoricoNotaFiscalService historicoNotaFiscalService;
    private final HistoricoNotaFiscalMapper historicoNotaFiscalMapper;

    @Autowired
    public HistoricoNotaFiscalController(HistoricoNotaFiscalService historicoNotaFiscalService, HistoricoNotaFiscalMapper historicoNotaFiscalMapper) {
        this.historicoNotaFiscalService = historicoNotaFiscalService;
        this.historicoNotaFiscalMapper = historicoNotaFiscalMapper;
    }

    @GetMapping
    public ResponseEntity<List<HistoricoNotaFiscalDTO>> listarTodos() {
        List<HistoricoNotaFiscal> historicos = historicoNotaFiscalService.listarTodos();
        List<HistoricoNotaFiscalDTO> historicosDTO = historicoNotaFiscalMapper.toDtoList(historicos);
        return ResponseEntity.ok(historicosDTO);
    }

    @GetMapping("/nota-fiscal/{notaFiscalId}")
    public ResponseEntity<List<HistoricoNotaFiscalDTO>> listarPorNotaFiscal(@PathVariable Long notaFiscalId) {
        List<HistoricoNotaFiscal> historicos = historicoNotaFiscalService.listarPorNotaFiscal(notaFiscalId);
        List<HistoricoNotaFiscalDTO> historicosDTO = historicoNotaFiscalMapper.toDtoList(historicos);
        return ResponseEntity.ok(historicosDTO);
    }


    @GetMapping("/tipo-operacao/{tipoOperacao}")
    public ResponseEntity<List<HistoricoNotaFiscalDTO>> listarPorTipoOperacao(@PathVariable String tipoOperacao) {
        List<HistoricoNotaFiscal> historicos = historicoNotaFiscalService.listarPorTipoOperacao(tipoOperacao);
        List<HistoricoNotaFiscalDTO> historicosDTO = historicoNotaFiscalMapper.toDtoList(historicos);
        return ResponseEntity.ok(historicosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoricoNotaFiscalDTO> buscarPorId(@PathVariable Long id) {
        return historicoNotaFiscalService.buscarPorId(id)
                .map(historicoNotaFiscalMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody HistoricoNotaFiscalDTO historicoNotaFiscalDTO) {
        try {
            HistoricoNotaFiscal historicoNotaFiscal = historicoNotaFiscalMapper.toEntity(historicoNotaFiscalDTO);
            HistoricoNotaFiscal historicoCreated = historicoNotaFiscalService.salvar(historicoNotaFiscal);
            HistoricoNotaFiscalDTO historicoCreatedDTO = historicoNotaFiscalMapper.toDto(historicoCreated);
            return ResponseEntity.status(HttpStatus.CREATED).body(historicoCreatedDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody HistoricoNotaFiscalDTO historicoNotaFiscalDTO) {
        try {
            HistoricoNotaFiscal historicoNotaFiscal = historicoNotaFiscalMapper.toEntity(historicoNotaFiscalDTO);
            HistoricoNotaFiscal historicoAtualizado = historicoNotaFiscalService.atualizar(id, historicoNotaFiscal);
            HistoricoNotaFiscalDTO historicoAtualizadoDTO = historicoNotaFiscalMapper.toDto(historicoAtualizado);
            return ResponseEntity.ok(historicoAtualizadoDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            historicoNotaFiscalService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
