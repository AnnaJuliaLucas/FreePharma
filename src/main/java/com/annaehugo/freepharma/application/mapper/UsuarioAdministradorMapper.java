package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.administrativo.UsuarioAdministradorDTO;
import com.annaehugo.freepharma.application.dto.administrativo.UsuarioAdministradorResponseDTO;
import com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador;
import org.springframework.stereotype.Component;

@Component
public class UsuarioAdministradorMapper extends BaseMapper<UsuarioAdministrador, UsuarioAdministradorDTO> {

    @Override
    protected Class<UsuarioAdministrador> getEntityClass() {
        return UsuarioAdministrador.class;
    }

    @Override
    protected Class<UsuarioAdministradorDTO> getDtoClass() {
        return UsuarioAdministradorDTO.class;
    }

    @Override
    public UsuarioAdministradorDTO toDto(UsuarioAdministrador entity) {
        if (entity == null) {
            return null;
        }
        
        return super.toDto(entity);
    }
    
    public UsuarioAdministradorResponseDTO toResponseDto(UsuarioAdministrador entity) {
        if (entity == null) {
            return null;
        }
        
        UsuarioAdministradorResponseDTO dto = modelMapper.map(entity, UsuarioAdministradorResponseDTO.class);
        return dto;
    }
}