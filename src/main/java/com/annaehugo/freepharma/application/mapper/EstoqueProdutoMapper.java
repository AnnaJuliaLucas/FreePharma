package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.estoque.EstoqueProdutoDTO;
import com.annaehugo.freepharma.domain.entity.estoque.EstoqueProduto;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class EstoqueProdutoMapper extends BaseMapper<EstoqueProduto, EstoqueProdutoDTO> {

    @Override
    protected Class<EstoqueProduto> getEntityClass() {
        return EstoqueProduto.class;
    }

    @Override
    protected Class<EstoqueProdutoDTO> getDtoClass() {
        return EstoqueProdutoDTO.class;
    }

    @Override
    public EstoqueProdutoDTO toDto(EstoqueProduto entity) {
        if (entity == null) {
            return null;
        }
        
        EstoqueProdutoDTO dto = super.toDto(entity);
        
        // Garantir que valores BigDecimal não sejam null
        if (dto.getValorUnitario() == null) {
            dto.setValorUnitario(BigDecimal.ZERO);
        }
        
        if (dto.getValorTotal() == null) {
            dto.setValorTotal(BigDecimal.ZERO);
        }
        
        if (entity.getProdutoFornecedor() != null) {
            dto.setProdutoFornecedorId(entity.getProdutoFornecedor().getId());
        }
        
        if (entity.getProdutoReferencia() != null) {
            dto.setProdutoReferenciaId(entity.getProdutoReferencia().getId());
        }
        
        // Unidade é mapeada como objeto completo, não apenas ID
        // if (entity.getUnidade() != null) {
        //     dto.setUnidade(unidadeMapper.toDto(entity.getUnidade()));
        // }
        
        return dto;
    }
    
    @Override
    public EstoqueProduto toEntity(EstoqueProdutoDTO dto) {
        if (dto == null) {
            return null;
        }
        
        EstoqueProduto entity = super.toEntity(dto);
        
        // Garantir que valores BigDecimal não sejam null na entidade
        if (entity.getValorUnitario() == null) {
            entity.setValorUnitario(BigDecimal.ZERO);
        }
        
        if (entity.getValorTotal() == null) {
            entity.setValorTotal(BigDecimal.ZERO);
        }
        
        return entity;
    }
}