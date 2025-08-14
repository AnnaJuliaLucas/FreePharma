package com.annaehugo.freepharma.api.controllers.fiscal;

import com.annaehugo.freepharma.application.dto.fiscal.NotaFiscalItemDTO;
import com.annaehugo.freepharma.application.mapper.NotaFiscalItemMapper;
import com.annaehugo.freepharma.application.services.NotaFiscalService;
import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscalItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fiscal/nota-fiscal-item")
public class NotaFiscalItemController {

    private final NotaFiscalService notaFiscalService;
    private final NotaFiscalItemMapper notaFiscalItemMapper;

    @Autowired
    public NotaFiscalItemController(NotaFiscalService notaFiscalService, NotaFiscalItemMapper notaFiscalItemMapper) {
        this.notaFiscalService = notaFiscalService;
        this.notaFiscalItemMapper = notaFiscalItemMapper;
    }

    @GetMapping("/nota-fiscal/{notaFiscalId}")
    public ResponseEntity<List<NotaFiscalItemDTO>> listarItensPorNota(@PathVariable Long notaFiscalId) {
        List<NotaFiscalItem> itens = notaFiscalService.listarItensPorNota(notaFiscalId);
        List<NotaFiscalItemDTO> itensDTO = notaFiscalItemMapper.toDtoList(itens);
        return ResponseEntity.ok(itensDTO);
    }
}
