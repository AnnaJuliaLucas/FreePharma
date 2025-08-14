package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.estoque.CategoriaDTO;
import com.annaehugo.freepharma.domain.entity.estoque.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper extends BaseMapper<Categoria, CategoriaDTO> {

    @Override
    protected Class<Categoria> getEntityClass() {
        return Categoria.class;
    }

    @Override
    protected Class<CategoriaDTO> getDtoClass() {
        return CategoriaDTO.class;
    }

    @Override
    public CategoriaDTO toDto(Categoria entity) {
        if (entity == null) {
            return null;
        }
        
        return super.toDto(entity);
    }

    @Override
    public Categoria toEntity(CategoriaDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return super.toEntity(dto);
    }
}