package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.estoque.ProdutoReferenciaDTO;
import com.annaehugo.freepharma.domain.entity.estoque.ProdutoReferencia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class ProdutoReferenciaMapper extends BaseMapper<ProdutoReferencia, ProdutoReferenciaDTO> {

    @Autowired
    private CategoriaMapper categoriaMapper;
    
    @Autowired
    private ProdutoFornecedorMapper produtoFornecedorMapper;

    @Override
    protected Class<ProdutoReferencia> getEntityClass() {
        return ProdutoReferencia.class;
    }

    @Override
    protected Class<ProdutoReferenciaDTO> getDtoClass() {
        return ProdutoReferenciaDTO.class;
    }

    @Override
    public ProdutoReferenciaDTO toDto(ProdutoReferencia entity) {
        if (entity == null) {
            return null;
        }
        
        ProdutoReferenciaDTO dto = super.toDto(entity);
        
        if (entity.getCategoria() != null) {
            dto.setCategoria(categoriaMapper.toDto(entity.getCategoria()));
        }
        
        if (entity.getProdutosFornecedor() != null && !entity.getProdutosFornecedor().isEmpty()) {
            dto.setProdutosFornecedor(produtoFornecedorMapper.toDtoList(entity.getProdutosFornecedor()));
        }
        
        return dto;
    }

    @Override
    public ProdutoReferencia toEntity(ProdutoReferenciaDTO dto) {
        if (dto == null) {
            return null;
        }
        
        try {
            ProdutoReferencia entity = new ProdutoReferencia();
            
            // Mapear campos básicos manualmente para evitar problemas do ModelMapper
            entity.setCodigoInterno(dto.getCodigoInterno());
            entity.setNome(dto.getNome());
            entity.setDescricao(dto.getDescricao());
            entity.setEan(dto.getEan());
            entity.setNcm(dto.getNcm());
            entity.setCfop(dto.getCfop());
            entity.setUnidadeMedida(dto.getUnidadeMedida() != null ? dto.getUnidadeMedida() : "UN");
            entity.setStatus(dto.getStatus() != null ? dto.getStatus() : "ATIVO");
            entity.setValidade(dto.getValidade());
            entity.setAtivo(dto.isAtivo());
            
            // Mapear relacionamentos apenas se não forem null
            if (dto.getCategoria() != null) {
                entity.setCategoria(categoriaMapper.toEntity(dto.getCategoria()));
            }
            
            // Não mapear produtosFornecedor na criação para evitar problemas
            // if (dto.getProdutosFornecedor() != null && !dto.getProdutosFornecedor().isEmpty()) {
            //     entity.setProdutosFornecedor(produtoFornecedorMapper.toEntityList(dto.getProdutosFornecedor()));
            // }
            
            return entity;
        } catch (Exception e) {
            // Log do erro para debug
            System.err.println("Erro ao mapear ProdutoReferenciaDTO para entidade: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro no mapeamento do produto: " + e.getMessage(), e);
        }
    }
}