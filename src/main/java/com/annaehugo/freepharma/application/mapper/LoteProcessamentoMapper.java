package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.fiscal.LoteProcessamentoDTO;
import com.annaehugo.freepharma.domain.entity.fiscal.LoteProcessamento;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LoteProcessamentoMapper extends BaseMapper<LoteProcessamento, LoteProcessamentoDTO> {

    @Override
    public Class<LoteProcessamento> getEntityClass() {
        return LoteProcessamento.class;
    }

    @Override
    public Class<LoteProcessamentoDTO> getDtoClass() {
        return LoteProcessamentoDTO.class;
    }

    @Override
    public LoteProcessamentoDTO toDto(LoteProcessamento entity) {
        if (entity == null) return null;
        
        LoteProcessamentoDTO dto = new LoteProcessamentoDTO();
        dto.setId(entity.getId());
        dto.setCodigoLote(entity.getIdLote() != null ? entity.getIdLote().toString() : null);
        dto.setDataInicio(entity.getDataInicio());
        dto.setDataFim(entity.getDataFim());
        dto.setStatus(entity.getStatus());
        dto.setObservacoes(entity.getDetalhesErro());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setAtivo(entity.isAtivo());
        // Map other DTO-specific fields to appropriate defaults or null
        
        return dto;
    }

    @Override
    public LoteProcessamento toEntity(LoteProcessamentoDTO dto) {
        if (dto == null) return null;
        
        LoteProcessamento entity = new LoteProcessamento();
        entity.setId(dto.getId());
        entity.setDataInicio(dto.getDataInicio());
        entity.setDataFim(dto.getDataFim());
        entity.setStatus(dto.getStatus());
        entity.setDetalhesErro(dto.getObservacoes());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setAtivo(dto.isAtivo());
        // Map entity-specific fields from appropriate DTO fields
        
        return entity;
    }

    @Override
    public List<LoteProcessamentoDTO> toDtoList(List<LoteProcessamento> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<LoteProcessamento> toEntityList(List<LoteProcessamentoDTO> dtos) {
        if (dtos == null) return null;
        return dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }
}