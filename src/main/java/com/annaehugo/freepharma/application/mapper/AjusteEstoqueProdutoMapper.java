package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.estoque.AjusteEstoqueProdutoDTO;
import com.annaehugo.freepharma.domain.entity.estoque.AjusteEstoqueProduto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AjusteEstoqueProdutoMapper extends BaseMapper<AjusteEstoqueProduto, AjusteEstoqueProdutoDTO> {

    @Autowired
    private EstoqueProdutoMapper estoqueProdutoMapper;
    
    @Autowired
    private UsuarioAdministradorMapper usuarioAdministradorMapper;

    @Override
    protected Class<AjusteEstoqueProduto> getEntityClass() {
        return AjusteEstoqueProduto.class;
    }

    @Override
    protected Class<AjusteEstoqueProdutoDTO> getDtoClass() {
        return AjusteEstoqueProdutoDTO.class;
    }

    @Override
    public AjusteEstoqueProdutoDTO toDto(AjusteEstoqueProduto entity) {
        if (entity == null) {
            return null;
        }
        
        AjusteEstoqueProdutoDTO dto = super.toDto(entity);
        
        if (entity.getEstoqueProduto() != null) {
            dto.setEstoqueProdutoId(entity.getEstoqueProduto().getId());
            dto.setEstoqueProduto(estoqueProdutoMapper.toDto(entity.getEstoqueProduto()));
        }
        
        if (entity.getUsuarioResponsavel() != null) {
            dto.setUsuarioResponsavelId(entity.getUsuarioResponsavel().getId());
            dto.setUsuarioResponsavel(usuarioAdministradorMapper.toDto(entity.getUsuarioResponsavel()));
        }
        
        return dto;
    }

    @Override
    public AjusteEstoqueProduto toEntity(AjusteEstoqueProdutoDTO dto) {
        if (dto == null) {
            return null;
        }
        
        AjusteEstoqueProduto entity = super.toEntity(dto);
        
        if (dto.getEstoqueProduto() != null) {
            entity.setEstoqueProduto(estoqueProdutoMapper.toEntity(dto.getEstoqueProduto()));
        }
        
        if (dto.getUsuarioResponsavel() != null) {
            entity.setUsuarioResponsavel(usuarioAdministradorMapper.toEntity(dto.getUsuarioResponsavel()));
        }
        
        return entity;
    }
}