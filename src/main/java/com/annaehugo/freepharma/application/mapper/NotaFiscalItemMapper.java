package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.fiscal.NotaFiscalItemDTO;
import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscalItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotaFiscalItemMapper extends BaseMapper<NotaFiscalItem, NotaFiscalItemDTO> {

    @Override
    public Class<NotaFiscalItem> getEntityClass() {
        return NotaFiscalItem.class;
    }

    @Override
    public Class<NotaFiscalItemDTO> getDtoClass() {
        return NotaFiscalItemDTO.class;
    }

    @Override
    public NotaFiscalItemDTO toDto(NotaFiscalItem entity) {
        if (entity == null) return null;
        
        NotaFiscalItemDTO dto = new NotaFiscalItemDTO();
        dto.setId(entity.getId());
        dto.setDescricao(entity.getDescricao());
        dto.setQuantidade(entity.getQuantidade());
        dto.setValorUnitario(entity.getValorUnitario());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setAtivo(entity.isAtivo());
        dto.setNotaFiscalId(entity.getNotaFiscal() != null ? entity.getNotaFiscal().getId() : null);
        // Note: ProdutoReferencia relationship handled separately
        
        return dto;
    }

    @Override
    public NotaFiscalItem toEntity(NotaFiscalItemDTO dto) {
        if (dto == null) return null;
        
        NotaFiscalItem entity = new NotaFiscalItem();
        entity.setId(dto.getId());
        entity.setDescricao(dto.getDescricao());
        entity.setQuantidade(dto.getQuantidade());
        entity.setValorUnitario(dto.getValorUnitario());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setAtivo(dto.isAtivo());
        // Note: NotaFiscal and ProdutoReferencia relationships should be handled separately
        
        return entity;
    }

    @Override
    public List<NotaFiscalItemDTO> toDtoList(List<NotaFiscalItem> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<NotaFiscalItem> toEntityList(List<NotaFiscalItemDTO> dtos) {
        if (dtos == null) return null;
        return dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }
}