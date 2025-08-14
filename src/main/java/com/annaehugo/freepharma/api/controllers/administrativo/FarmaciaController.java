package com.annaehugo.freepharma.api.controllers.administrativo;

import com.annaehugo.freepharma.application.dto.administrativo.FarmaciaDTO;
import com.annaehugo.freepharma.application.mapper.FarmaciaMapper;
import com.annaehugo.freepharma.application.services.FarmaciaService;
import com.annaehugo.freepharma.domain.entity.administrativo.Farmacia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmacias")
public class FarmaciaController {

    private final FarmaciaService farmaciaService;
    private final FarmaciaMapper farmaciaMapper;

    @Autowired
    public FarmaciaController(FarmaciaService farmaciaService, FarmaciaMapper farmaciaMapper) {
        this.farmaciaService = farmaciaService;
        this.farmaciaMapper = farmaciaMapper;
    }

    @GetMapping
    public ResponseEntity<List<FarmaciaDTO>> listarTodas() {
        List<Farmacia> farmacias = farmaciaService.listarTodas();
        List<FarmaciaDTO> farmaciasDTO = farmaciaMapper.toDtoList(farmacias);
        return ResponseEntity.ok(farmaciasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FarmaciaDTO> buscarPorId(@PathVariable Long id) {
        return farmaciaService.buscarPorId(id)
                .map(farmaciaMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<FarmaciaDTO> buscarPorCnpj(@PathVariable String cnpj) {
        return farmaciaService.buscarPorCnpj(cnpj)
                .map(farmaciaMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FarmaciaDTO>> buscarPorStatus(@PathVariable String status) {
        List<Farmacia> farmacias = farmaciaService.buscarPorStatus(status);
        List<FarmaciaDTO> farmaciasDTO = farmaciaMapper.toDtoList(farmacias);
        return ResponseEntity.ok(farmaciasDTO);
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody FarmaciaDTO farmaciaDTO) {
        try {
            Farmacia farmacia = farmaciaMapper.toEntity(farmaciaDTO);
            Farmacia farmaciaCreated = farmaciaService.salvar(farmacia);
            FarmaciaDTO farmaciaCreatedDTO = farmaciaMapper.toDto(farmaciaCreated);
            return ResponseEntity.status(HttpStatus.CREATED).body(farmaciaCreatedDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody FarmaciaDTO farmaciaDTO) {
        try {
            Farmacia farmacia = farmaciaMapper.toEntity(farmaciaDTO);
            Farmacia farmaciaAtualizada = farmaciaService.atualizar(id, farmacia);
            FarmaciaDTO farmaciaAtualizadaDTO = farmaciaMapper.toDto(farmaciaAtualizada);
            return ResponseEntity.ok(farmaciaAtualizadaDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            farmaciaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            farmaciaService.ativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        try {
            farmaciaService.inativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
