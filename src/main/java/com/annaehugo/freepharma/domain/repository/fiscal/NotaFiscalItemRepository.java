package com.annaehugo.freepharma.domain.repository.fiscal;

import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaFiscalItemRepository extends JpaRepository<NotaFiscalItem, Long> {
    List<NotaFiscalItem> findByNotaFiscalId(Long notaFiscalId);
}
