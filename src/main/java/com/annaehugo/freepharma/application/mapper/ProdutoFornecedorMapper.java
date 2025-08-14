package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.estoque.ProdutoFornecedorDTO;
import com.annaehugo.freepharma.domain.entity.estoque.ProdutoFornecedor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class ProdutoFornecedorMapper extends BaseMapper<ProdutoFornecedor, ProdutoFornecedorDTO> {

    @Override
    protected Class<ProdutoFornecedor> getEntityClass() {
        return ProdutoFornecedor.class;
    }

    @Override
    protected Class<ProdutoFornecedorDTO> getDtoClass() {
        return ProdutoFornecedorDTO.class;
    }

    @Override
    public ProdutoFornecedorDTO toDto(ProdutoFornecedor entity) {
        if (entity == null) {
            return null;
        }
        
        ProdutoFornecedorDTO dto = super.toDto(entity);
        
        // Garantir que valores BigDecimal não sejam null
        if (dto.getPrecoCompra() == null) {
            dto.setPrecoCompra(BigDecimal.ZERO);
        }
        
        if (dto.getPrecoVenda() == null) {
            dto.setPrecoVenda(BigDecimal.ZERO);
        }
        
        if (entity.getProdutoReferencia() != null) {
            dto.setProdutoReferenciaId(entity.getProdutoReferencia().getId());
        }
        
        if (entity.getFornecedor() != null) {
            dto.setFornecedorId(entity.getFornecedor().getId());
        }
        
        return dto;
    }

    @Override
    public ProdutoFornecedor toEntity(ProdutoFornecedorDTO dto) {
        if (dto == null) {
            return null;
        }
        
        ProdutoFornecedor entity = super.toEntity(dto);
        
        // Garantir que valores BigDecimal não sejam null na entidade
        if (entity.getPrecoCompra() == null) {
            entity.setPrecoCompra(BigDecimal.ZERO);
        }
        
        if (entity.getPrecoVenda() == null) {
            entity.setPrecoVenda(BigDecimal.ZERO);
        }
        
        return entity;
    }
}