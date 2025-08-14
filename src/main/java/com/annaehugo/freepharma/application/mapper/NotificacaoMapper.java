package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.compartilhado.NotificacaoDTO;
import com.annaehugo.freepharma.domain.entity.compartilhado.Notificacao;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoMapper extends BaseMapper<Notificacao, NotificacaoDTO> {

    @Override
    protected Class<Notificacao> getEntityClass() {
        return Notificacao.class;
    }

    @Override
    protected Class<NotificacaoDTO> getDtoClass() {
        return NotificacaoDTO.class;
    }

    @Override
    public NotificacaoDTO toDto(Notificacao entity) {
        if (entity == null) {
            return null;
        }
        
        NotificacaoDTO dto = super.toDto(entity);
        
        if (entity.getDestinatario() != null) {
            dto.setDestinatarioId(entity.getDestinatario().getId());
        }
        
        if (entity.getInconsistencia() != null) {
            dto.setInconsistenciaId(entity.getInconsistencia().getId());
        }
        
        return dto;
    }

    @Override
    public Notificacao toEntity(NotificacaoDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return super.toEntity(dto);
    }
}