//package com.annaehugo.freepharma.domain.repository.fiscal;
//
//import com.annaehugo.freepharma.domain.entity.fiscal.ImportacaoNFe;
//import com.annaehugo.freepharma.domain.entity.fiscal.StatusImportacao;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.Date;
//import java.util.List;
//
//@Repository
//public interface ImportacaoNFeRepository extends JpaRepository<ImportacaoNFe, Long> {
//
//    List<ImportacaoNFe> findByStatus(StatusImportacao status);
//    List<ImportacaoNFe> findByDataImportacaoBetween(Date inicio, Date fim);
//    List<ImportacaoNFe> findByUnidadeId(Long unidadeId);
//    List<ImportacaoNFe> findByUsuarioImportacaoId(Long usuarioId);
//
//    @Query("SELECT i FROM ImportacaoNFe i WHERE i.status = :status AND i.dataImportacao >= :data")
//    List<ImportacaoNFe> findByStatusAndDataImportacaoAfter(StatusImportacao status, Date data);
//
//    @Query("SELECT COUNT(i) FROM ImportacaoNFe i WHERE i.status = 'CONCLUIDA' AND i.dataImportacao >= :inicio AND i.dataImportacao <= :fim")
//    Long countImportacoesConcluidasPeriodo(Date inicio, Date fim);
//
//    @Query("SELECT COUNT(i) FROM ImportacaoNFe i WHERE i.status = 'ERRO' AND i.dataImportacao >= :inicio AND i.dataImportacao <= :fim")
//    Long countImportacoesComErroPeriodo(Date inicio, Date fim);
//}