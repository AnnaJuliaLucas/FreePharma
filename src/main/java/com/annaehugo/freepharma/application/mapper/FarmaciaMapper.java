package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.administrativo.FarmaciaDTO;
import com.annaehugo.freepharma.domain.entity.administrativo.Farmacia;
import org.springframework.stereotype.Component;

@Component
public class FarmaciaMapper extends BaseMapper<Farmacia, FarmaciaDTO> {

    @Override
    protected Class<Farmacia> getEntityClass() {
        return Farmacia.class;
    }

    @Override
    protected Class<FarmaciaDTO> getDtoClass() {
        return FarmaciaDTO.class;
    }

    @Override
    public FarmaciaDTO toDto(Farmacia entity) {
        if (entity == null) {
            return null;
        }
        
        FarmaciaDTO dto = super.toDto(entity);
        
        if (entity.getConfiguracaoFiscal() != null) {
            dto.setConfiguracaoFiscalId(entity.getConfiguracaoFiscal().getId());
        }
        
        return dto;
    }

    @Override
    public Farmacia toEntity(FarmaciaDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Farmacia entity = super.toEntity(dto);
        return entity;
    }
}