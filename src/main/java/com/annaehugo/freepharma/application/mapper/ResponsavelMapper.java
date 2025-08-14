package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.administrativo.ResponsavelDTO;
import com.annaehugo.freepharma.domain.entity.administrativo.Responsavel;
import com.annaehugo.freepharma.domain.entity.administrativo.Farmacia;
import org.springframework.stereotype.Component;

@Component
public class ResponsavelMapper extends BaseMapper<Responsavel, ResponsavelDTO> {

    @Override
    protected Class<Responsavel> getEntityClass() {
        return Responsavel.class;
    }

    @Override
    protected Class<ResponsavelDTO> getDtoClass() {
        return ResponsavelDTO.class;
    }

    @Override
    public ResponsavelDTO toDto(Responsavel entity) {
        if (entity == null) {
            return null;
        }
        
        ResponsavelDTO dto = super.toDto(entity);
        
        if (entity.getFarmacia() != null) {
            dto.setFarmaciaId(entity.getFarmacia().getId());
        }
        
        return dto;
    }

    @Override
    public Responsavel toEntity(ResponsavelDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Responsavel entity = super.toEntity(dto);
        
        // Set Farmacia entity based on farmaciaId
        if (dto.getFarmaciaId() != null) {
            Farmacia farmacia = new Farmacia();
            farmacia.setId(dto.getFarmaciaId());
            entity.setFarmacia(farmacia);
        }
        
        return entity;
    }
}