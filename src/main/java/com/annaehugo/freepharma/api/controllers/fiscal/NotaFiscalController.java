package com.annaehugo.freepharma.api.controllers.fiscal;

import com.annaehugo.freepharma.application.dto.fiscal.NotaFiscalDTO;
import com.annaehugo.freepharma.application.dto.fiscal.NotaFiscalItemDTO;
import com.annaehugo.freepharma.application.dto.fiscal.InconsistenciaDTO;
import com.annaehugo.freepharma.application.mapper.NotaFiscalMapper;
import com.annaehugo.freepharma.application.services.NotaFiscalService;
import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscal;
import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscalItem;
import com.annaehugo.freepharma.domain.entity.fiscal.Inconsistencia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/notas-fiscais")
public class NotaFiscalController {

    private final NotaFiscalService notaFiscalService;
    private final NotaFiscalMapper notaFiscalMapper;

    @Autowired
    public NotaFiscalController(NotaFiscalService notaFiscalService, NotaFiscalMapper notaFiscalMapper) {
        this.notaFiscalService = notaFiscalService;
        this.notaFiscalMapper = notaFiscalMapper;
    }

    @GetMapping
    public ResponseEntity<List<NotaFiscalDTO>> listarTodas() {
        List<NotaFiscal> notas = notaFiscalService.listarTodas();
        List<NotaFiscalDTO> notasDTO = notaFiscalMapper.toDtoList(notas);
        return ResponseEntity.ok(notasDTO);
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<NotaFiscalDTO>> listarComPaginacao(Pageable pageable) {
        Page<NotaFiscal> notas = notaFiscalService.listarComPaginacao(pageable);
        Page<NotaFiscalDTO> notasDTO = notas.map(notaFiscalMapper::toDto);
        return ResponseEntity.ok(notasDTO);
    }


    @GetMapping("/unidade/{unidadeId}")
    public ResponseEntity<List<NotaFiscalDTO>> listarPorUnidade(@PathVariable Long unidadeId) {
        List<NotaFiscal> notas = notaFiscalService.listarPorUnidade(unidadeId);
        List<NotaFiscalDTO> notasDTO = notaFiscalMapper.toDtoList(notas);
        return ResponseEntity.ok(notasDTO);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotaFiscal>> listarPorStatus(@PathVariable String status) {
        List<NotaFiscal> notas = notaFiscalService.listarPorStatus(status);
        return ResponseEntity.ok(notas);
    }

    @GetMapping("/tipo/{tipoOperacao}")
    public ResponseEntity<List<NotaFiscal>> listarPorTipoOperacao(@PathVariable String tipoOperacao) {
        List<NotaFiscal> notas = notaFiscalService.listarPorTipoOperacao(tipoOperacao);
        return ResponseEntity.ok(notas);
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<NotaFiscal>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataFim) {
        List<NotaFiscal> notas = notaFiscalService.listarPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(notas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotaFiscalDTO> buscarPorId(@PathVariable Long id) {
        return notaFiscalService.buscarPorId(id)
                .map(notaFiscalMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/chave/{chaveAcesso}")
    public ResponseEntity<NotaFiscal> buscarPorChaveAcesso(@PathVariable String chaveAcesso) {
        return notaFiscalService.buscarPorChaveAcesso(chaveAcesso)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<NotaFiscal> buscarPorNumero(@PathVariable String numero) {
        return notaFiscalService.buscarPorNumero(numero)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/itens")
    public ResponseEntity<List<NotaFiscalItem>> listarItensPorNota(@PathVariable Long id) {
        List<NotaFiscalItem> itens = notaFiscalService.listarItensPorNota(id);
        return ResponseEntity.ok(itens);
    }

    @GetMapping("/{id}/inconsistencias")
    public ResponseEntity<List<Inconsistencia>> listarInconsistenciasPorNota(@PathVariable Long id) {
        List<Inconsistencia> inconsistencias = notaFiscalService.listarInconsistenciasPorNota(id);
        return ResponseEntity.ok(inconsistencias);
    }

//    @GetMapping("/com-inconsistencias")
//    public ResponseEntity<List<NotaFiscal>> listarComInconsistencias() {
//        List<NotaFiscal> notas = notaFiscalService.listarComInconsistencias();
//        return ResponseEntity.ok(notas);
//    }

    @GetMapping("/fornecedor/{fornecedorId}")
    public ResponseEntity<List<NotaFiscal>> buscarPorFornecedor(@PathVariable Long fornecedorId) {
        List<NotaFiscal> notas = notaFiscalService.buscarPorFornecedor(fornecedorId);
        return ResponseEntity.ok(notas);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<NotaFiscal>> buscarPorCliente(@PathVariable Long clienteId) {
        List<NotaFiscal> notas = notaFiscalService.buscarPorCliente(clienteId);
        return ResponseEntity.ok(notas);
    }

//    // Endpoints para dashboards e relatórios
//    @GetMapping("/estatisticas/periodo")
//    public ResponseEntity<Map<String, Object>> obterEstatisticasPorPeriodo(
//            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataInicio,
//            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataFim) {
//
//        Map<String, Object> estatisticas = new HashMap<>();
//        estatisticas.put("totalNotas", notaFiscalService.contarNotasPorPeriodo(dataInicio, dataFim));
//        estatisticas.put("valorTotal", notaFiscalService.calcularValorTotalPorPeriodo(dataInicio, dataFim));
//        estatisticas.put("notasComInconsistencias", notaFiscalService.contarNotasComInconsistencias());
//
//        return ResponseEntity.ok(estatisticas);
//    }

//    @GetMapping("/dashboard")
//    public ResponseEntity<Map<String, Object>> obterDadosDashboard() {
//        Map<String, Object> dashboard = new HashMap<>();
//
//        // Estatísticas gerais
//        dashboard.put("totalNotasComInconsistencias", notaFiscalService.contarNotasComInconsistencias());
//
//        // Notas recentes com inconsistências
//        dashboard.put("notasComInconsistencias", notaFiscalService.listarComInconsistencias());
//
//        return ResponseEntity.ok(dashboard);
//    }
}
