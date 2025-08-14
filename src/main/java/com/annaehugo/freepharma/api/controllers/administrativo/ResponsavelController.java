package com.annaehugo.freepharma.api.controllers.administrativo;

import com.annaehugo.freepharma.application.dto.administrativo.ResponsavelDTO;
import com.annaehugo.freepharma.application.mapper.ResponsavelMapper;
import com.annaehugo.freepharma.application.services.ResponsavelService;
import com.annaehugo.freepharma.domain.entity.administrativo.Responsavel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/responsaveis")
public class ResponsavelController {

    private final ResponsavelService responsavelService;
    private final ResponsavelMapper responsavelMapper;

    @Autowired
    public ResponsavelController(ResponsavelService responsavelService, ResponsavelMapper responsavelMapper) {
        this.responsavelService = responsavelService;
        this.responsavelMapper = responsavelMapper;
    }

    @GetMapping
    public ResponseEntity<List<ResponsavelDTO>> listarTodos() {
        List<Responsavel> responsaveis = responsavelService.listarTodos();
        List<ResponsavelDTO> responsaveisDTO = responsavelMapper.toDtoList(responsaveis);
        return ResponseEntity.ok(responsaveisDTO);
    }


    @GetMapping("/farmacia/{farmaciaId}")
    public ResponseEntity<List<ResponsavelDTO>> listarPorFarmacia(@PathVariable Long farmaciaId) {
        List<Responsavel> responsaveis = responsavelService.listarPorFarmacia(farmaciaId);
        List<ResponsavelDTO> responsaveisDTO = responsavelMapper.toDtoList(responsaveis);
        return ResponseEntity.ok(responsaveisDTO);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<ResponsavelDTO>> listarAtivos() {
        List<Responsavel> responsaveis = responsavelService.listarAtivos();
        List<ResponsavelDTO> responsaveisDTO = responsavelMapper.toDtoList(responsaveis);
        return ResponseEntity.ok(responsaveisDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponsavelDTO> buscarPorId(@PathVariable Long id) {
        return responsavelService.buscarPorId(id)
                .map(responsavelMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ResponsavelDTO> buscarPorCpf(@PathVariable String cpf) {
        return responsavelService.buscarPorCpf(cpf)
                .map(responsavelMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ResponsavelDTO> buscarPorEmail(@PathVariable String email) {
        return responsavelService.buscarPorEmail(email)
                .map(responsavelMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody ResponsavelDTO responsavelDTO) {
        try {
            Responsavel responsavel = responsavelMapper.toEntity(responsavelDTO);
            Responsavel responsavelCreated = responsavelService.salvar(responsavel);
            ResponsavelDTO responsavelCreatedDTO = responsavelMapper.toDto(responsavelCreated);
            return ResponseEntity.status(HttpStatus.CREATED).body(responsavelCreatedDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody ResponsavelDTO responsavelDTO) {
        try {
            Responsavel responsavel = responsavelMapper.toEntity(responsavelDTO);
            Responsavel responsavelAtualizado = responsavelService.atualizar(id, responsavel);
            ResponsavelDTO responsavelAtualizadoDTO = responsavelMapper.toDto(responsavelAtualizado);
            return ResponseEntity.ok(responsavelAtualizadoDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            responsavelService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            responsavelService.ativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        try {
            responsavelService.inativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
