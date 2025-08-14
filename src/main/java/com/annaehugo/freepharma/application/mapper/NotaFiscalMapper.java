package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.fiscal.NotaFiscalDTO;
import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscal;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class NotaFiscalMapper extends BaseMapper<NotaFiscal, NotaFiscalDTO> {

    @Override
    protected Class<NotaFiscal> getEntityClass() {
        return NotaFiscal.class;
    }

    @Override
    protected Class<NotaFiscalDTO> getDtoClass() {
        return NotaFiscalDTO.class;
    }

    @Override
    public NotaFiscalDTO toDto(NotaFiscal entity) {
        if (entity == null) {
            return null;
        }
        
        NotaFiscalDTO dto = super.toDto(entity);
        
        // Garantir que valorTotal não seja null
        if (dto.getValorTotal() == null) {
            dto.setValorTotal(BigDecimal.ZERO);
        }
        
        if (entity.getLoteProcessamento() != null) {
            dto.setLoteProcessamentoId(entity.getLoteProcessamento().getId());
        }

        if (entity.getImportacaoNFe() != null) {
            dto.setImportacaoNFeId(entity.getImportacaoNFe().getId());
        }
        
        // Relacionamentos são mapeados como objetos completos, não apenas IDs
        // if (entity.getUnidade() != null) {
        //     dto.setUnidade(unidadeMapper.toDto(entity.getUnidade()));
        // }
        // 
        // if (entity.getFornecedor() != null) {
        //     dto.setFornecedor(fornecedorMapper.toDto(entity.getFornecedor()));
        // }
        // 
        // if (entity.getCliente() != null) {
        //     dto.setCliente(clienteMapper.toDto(entity.getCliente()));
        // }
        
        return dto;
    }
    
    @Override
    public NotaFiscal toEntity(NotaFiscalDTO dto) {
        if (dto == null) {
            return null;
        }
        
        NotaFiscal entity = super.toEntity(dto);
        
        // Garantir que valorTotal não seja null na entidade
        if (entity.getValorTotal() == null) {
            entity.setValorTotal(BigDecimal.ZERO);
        }
        
        return entity;
    }
}