package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.fiscal.InconsistenciaDTO;
import com.annaehugo.freepharma.domain.entity.fiscal.Inconsistencia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InconsistenciaMapper extends BaseMapper<Inconsistencia, InconsistenciaDTO> {

    @Autowired
    private NotificacaoMapper notificacaoMapper;

    @Override
    protected Class<Inconsistencia> getEntityClass() {
        return Inconsistencia.class;
    }

    @Override
    protected Class<InconsistenciaDTO> getDtoClass() {
        return InconsistenciaDTO.class;
    }

    @Override
    public InconsistenciaDTO toDto(Inconsistencia entity) {
        if (entity == null) {
            return null;
        }
        
        InconsistenciaDTO dto = super.toDto(entity);
        
        if (entity.getNotaFiscal() != null) {
            dto.setNotaFiscalId(entity.getNotaFiscal().getId());
        }
        
        if (entity.getImportacaoNFe() != null) {
            dto.setImportacaoNFeId(entity.getImportacaoNFe().getId());
        }
        
        if (entity.getNotificacoes() != null) {
            dto.setNotificacoes(notificacaoMapper.toDtoList(entity.getNotificacoes()));
        }
        
        return dto;
    }

    @Override
    public Inconsistencia toEntity(InconsistenciaDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Inconsistencia entity = super.toEntity(dto);
        
        if (dto.getNotificacoes() != null) {
            entity.setNotificacoes(notificacaoMapper.toEntityList(dto.getNotificacoes()));
        }
        
        return entity;
    }
}