package com.annaehugo.freepharma.domain.repository.estoque;

import com.annaehugo.freepharma.domain.entity.estoque.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    
    Optional<Medicamento> findByCodigoInterno(String codigoInterno);
    
    Optional<Medicamento> findByEan(String ean);
    
    Optional<Medicamento> findByRegistroAnvisa(String registroAnvisa);
    
    List<Medicamento> findByPrincipioAtivoContainingIgnoreCase(String principioAtivo);
    
    List<Medicamento> findByLaboratorioContainingIgnoreCase(String laboratorio);
    
    List<Medicamento> findByControladoTrue();
    
    List<Medicamento> findByGenericoTrue();

}
