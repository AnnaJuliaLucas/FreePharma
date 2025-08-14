package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.estoque.HistoricoValorProdutoDTO;
import com.annaehugo.freepharma.domain.entity.estoque.HistoricoValorProduto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HistoricoValorProdutoMapper extends BaseMapper<HistoricoValorProduto, HistoricoValorProdutoDTO> {

    @Autowired
    private EstoqueProdutoMapper estoqueProdutoMapper;
    
    @Autowired
    private UsuarioAdministradorMapper usuarioAdministradorMapper;

    @Override
    protected Class<HistoricoValorProduto> getEntityClass() {
        return HistoricoValorProduto.class;
    }

    @Override
    protected Class<HistoricoValorProdutoDTO> getDtoClass() {
        return HistoricoValorProdutoDTO.class;
    }

    @Override
    public HistoricoValorProdutoDTO toDto(HistoricoValorProduto entity) {
        if (entity == null) {
            return null;
        }
        
        HistoricoValorProdutoDTO dto = super.toDto(entity);
        
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
    public HistoricoValorProduto toEntity(HistoricoValorProdutoDTO dto) {
        if (dto == null) {
            return null;
        }
        
        HistoricoValorProduto entity = super.toEntity(dto);
        
        if (dto.getEstoqueProduto() != null) {
            entity.setEstoqueProduto(estoqueProdutoMapper.toEntity(dto.getEstoqueProduto()));
        }
        
        if (dto.getUsuarioResponsavel() != null) {
            entity.setUsuarioResponsavel(usuarioAdministradorMapper.toEntity(dto.getUsuarioResponsavel()));
        }
        
        return entity;
    }
}