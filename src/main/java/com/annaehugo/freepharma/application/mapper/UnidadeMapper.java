package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.administrativo.UnidadeDTO;
import com.annaehugo.freepharma.domain.entity.administrativo.Unidade;
import com.annaehugo.freepharma.domain.entity.administrativo.Farmacia;
import com.annaehugo.freepharma.domain.entity.administrativo.Responsavel;
import org.springframework.stereotype.Component;

@Component
public class UnidadeMapper extends BaseMapper<Unidade, UnidadeDTO> {

    @Override
    protected Class<Unidade> getEntityClass() {
        return Unidade.class;
    }

    @Override
    protected Class<UnidadeDTO> getDtoClass() {
        return UnidadeDTO.class;
    }

    @Override
    public UnidadeDTO toDto(Unidade entity) {
        if (entity == null) {
            return null;
        }
        
        UnidadeDTO dto = super.toDto(entity);
        
        if (entity.getFarmacia() != null) {
            dto.setFarmaciaId(entity.getFarmacia().getId());
        }
        
        if (entity.getResponsavelLocal() != null) {
            dto.setResponsavelLocalId(entity.getResponsavelLocal().getId());
        }
        
        return dto;
    }

    @Override
    public Unidade toEntity(UnidadeDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Unidade entity = super.toEntity(dto);
        
        // Set Farmacia entity based on farmaciaId
        if (dto.getFarmaciaId() != null) {
            Farmacia farmacia = new Farmacia();
            farmacia.setId(dto.getFarmaciaId());
            entity.setFarmacia(farmacia);
        }
        
        // Set ResponsavelLocal entity based on responsavelLocalId
        if (dto.getResponsavelLocalId() != null) {
            Responsavel responsavel = new Responsavel();
            responsavel.setId(dto.getResponsavelLocalId());
            entity.setResponsavelLocal(responsavel);
        }
        
        return entity;
    }
}