package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.fiscal.HistoricoNotaFiscalDTO;
import com.annaehugo.freepharma.domain.entity.fiscal.HistoricoNotaFiscal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HistoricoNotaFiscalMapper extends BaseMapper<HistoricoNotaFiscal, HistoricoNotaFiscalDTO> {

    @Autowired
    private UsuarioAdministradorMapper usuarioAdministradorMapper;

    @Override
    protected Class<HistoricoNotaFiscal> getEntityClass() {
        return HistoricoNotaFiscal.class;
    }

    @Override
    protected Class<HistoricoNotaFiscalDTO> getDtoClass() {
        return HistoricoNotaFiscalDTO.class;
    }

    @Override
    public HistoricoNotaFiscalDTO toDto(HistoricoNotaFiscal entity) {
        if (entity == null) {
            return null;
        }
        
        HistoricoNotaFiscalDTO dto = super.toDto(entity);
        
        if (entity.getNotaFiscal() != null) {
            dto.setNotaFiscalId(entity.getNotaFiscal().getId());
        }
        
        if (entity.getUsuarioResponsavel() != null) {
            dto.setUsuarioResponsavelId(entity.getUsuarioResponsavel().getId());
            dto.setUsuarioResponsavel(usuarioAdministradorMapper.toDto(entity.getUsuarioResponsavel()));
        }
        
        return dto;
    }

    @Override
    public HistoricoNotaFiscal toEntity(HistoricoNotaFiscalDTO dto) {
        if (dto == null) {
            return null;
        }
        
        HistoricoNotaFiscal entity = super.toEntity(dto);
        
        if (dto.getUsuarioResponsavel() != null) {
            entity.setUsuarioResponsavel(usuarioAdministradorMapper.toEntity(dto.getUsuarioResponsavel()));
        }
        
        return entity;
    }
}