package com.annaehugo.freepharma.application.mapper;

import com.annaehugo.freepharma.application.dto.estoque.FornecedorDTO;
import com.annaehugo.freepharma.domain.entity.estoque.Fornecedor;
import org.springframework.stereotype.Component;

@Component
public class FornecedorMapper extends BaseMapper<Fornecedor, FornecedorDTO> {

    @Override
    protected Class<Fornecedor> getEntityClass() {
        return Fornecedor.class;
    }

    @Override
    protected Class<FornecedorDTO> getDtoClass() {
        return FornecedorDTO.class;
    }
}