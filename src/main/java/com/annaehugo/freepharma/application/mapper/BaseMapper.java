package com.annaehugo.freepharma.application.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseMapper<E, D> {

    @Autowired
    protected ModelMapper modelMapper;

    protected abstract Class<E> getEntityClass();
    protected abstract Class<D> getDtoClass();

    public D toDto(E entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, getDtoClass());
    }

    public E toEntity(D dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, getEntityClass());
    }

    public List<D> toDtoList(List<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<E> toEntityList(List<D> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}