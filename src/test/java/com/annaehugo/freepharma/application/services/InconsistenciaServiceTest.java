package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.fiscal.Inconsistencia;
import com.annaehugo.freepharma.domain.entity.fiscal.TipoInconsistencia;
import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscal;
import com.annaehugo.freepharma.domain.repository.fiscal.InconsistenciaRepository;
import com.annaehugo.freepharma.domain.repository.fiscal.NotaFiscalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InconsistenciaService Tests")
class InconsistenciaServiceTest {

    @Mock
    private InconsistenciaRepository inconsistenciaRepository;

    @Mock
    private NotaFiscalRepository notaFiscalRepository;

    private InconsistenciaService inconsistenciaService;

    private Inconsistencia inconsistencia;
    private NotaFiscal notaFiscal;
    private TipoInconsistencia tipoInconsistencia;

    @BeforeEach
    void setUp() {
        inconsistenciaService = new InconsistenciaService(
            inconsistenciaRepository, notaFiscalRepository
        );

        notaFiscal = new NotaFiscal();
        notaFiscal.setId(1L);
        notaFiscal.setNumero("000000001");

        tipoInconsistencia = TipoInconsistencia.VALOR_TOTAL_DIVERGENTE;

        inconsistencia = new Inconsistencia();
        inconsistencia.setId(1L);
        inconsistencia.setNotaFiscal(notaFiscal);
        inconsistencia.setTipo(tipoInconsistencia);
        inconsistencia.setDescricao("Valor total divergente entre XML e cálculo");
        inconsistencia.setSeveridade("ALTA");
        inconsistencia.setStatus("PENDENTE");
        inconsistencia.setDataDeteccao(new Date());
    }

    @Test
    @DisplayName("Should list all inconsistencies successfully")
    void shouldListAllInconsistenciesSuccessfully() {
        // Given
        List<Inconsistencia> inconsistencias = Arrays.asList(inconsistencia);
        when(inconsistenciaRepository.findAll()).thenReturn(inconsistencias);

        // When
        List<Inconsistencia> result = inconsistenciaService.listarTodas();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(inconsistencia);
        verify(inconsistenciaRepository).findAll();
    }

    @Test
    @DisplayName("Should list inconsistencies by fiscal note successfully")
    void shouldListInconsistenciesByFiscalNoteSuccessfully() {
        // Given
        List<Inconsistencia> inconsistencias = Arrays.asList(inconsistencia);
        when(inconsistenciaRepository.findByNotaFiscalId(1L)).thenReturn(inconsistencias);

        // When
        List<Inconsistencia> result = inconsistenciaService.listarPorNotaFiscal(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(inconsistencia);
        verify(inconsistenciaRepository).findByNotaFiscalId(1L);
    }

    @Test
    @DisplayName("Should list inconsistencies by type successfully")
    void shouldListInconsistenciesByTypeSuccessfully() {
        // Given
        List<Inconsistencia> inconsistencias = Arrays.asList(inconsistencia);
        when(inconsistenciaRepository.findByTipo(tipoInconsistencia)).thenReturn(inconsistencias);

        // When
        List<Inconsistencia> result = inconsistenciaService.listarPorTipo(tipoInconsistencia);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(inconsistencia);
        verify(inconsistenciaRepository).findByTipo(tipoInconsistencia);
    }

    @Test
    @DisplayName("Should list inconsistencies by status successfully")
    void shouldListInconsistenciesByStatusSuccessfully() {
        // Given
        String status = "PENDENTE";
        List<Inconsistencia> inconsistencias = Arrays.asList(inconsistencia);
        when(inconsistenciaRepository.findByStatus(status)).thenReturn(inconsistencias);

        // When
        List<Inconsistencia> result = inconsistenciaService.listarPorStatus(status);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(inconsistencia);
        verify(inconsistenciaRepository).findByStatus(status);
    }

    @Test
    @DisplayName("Should list unresolved inconsistencies successfully")
    void shouldListUnresolvedInconsistenciesSuccessfully() {
        // Given
        List<Inconsistencia> inconsistencias = Arrays.asList(inconsistencia);
        when(inconsistenciaRepository.findByStatusNot("RESOLVIDA")).thenReturn(inconsistencias);

        // When
        List<Inconsistencia> result = inconsistenciaService.listarNaoResolvidas();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(inconsistencia);
        verify(inconsistenciaRepository).findByStatusNot("RESOLVIDA");
    }

    @Test
    @DisplayName("Should find inconsistency by id successfully")
    void shouldFindInconsistencyByIdSuccessfully() {
        // Given
        when(inconsistenciaRepository.findById(1L)).thenReturn(Optional.of(inconsistencia));

        // When
        Optional<Inconsistencia> result = inconsistenciaService.buscarPorId(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(inconsistencia);
        verify(inconsistenciaRepository).findById(1L);
    }

    @Test
    @DisplayName("Should save inconsistency successfully")
    void shouldSaveInconsistencySuccessfully() {
        // Given
        Inconsistencia novaInconsistencia = new Inconsistencia();
        novaInconsistencia.setNotaFiscal(notaFiscal);
        novaInconsistencia.setTipo(tipoInconsistencia);
        novaInconsistencia.setDescricao("Nova inconsistência");
        novaInconsistencia.setSeveridade("MEDIA");

        when(notaFiscalRepository.existsById(1L)).thenReturn(true);
        when(inconsistenciaRepository.save(any(Inconsistencia.class))).thenReturn(novaInconsistencia);

        // When
        Inconsistencia result = inconsistenciaService.salvar(novaInconsistencia);

        // Then
        assertThat(result).isNotNull();
        assertThat(novaInconsistencia.getDataDeteccao()).isNotNull();
        assertThat(novaInconsistencia.getStatus()).isEqualTo("PENDENTE");
        verify(notaFiscalRepository).existsById(1L);
        verify(inconsistenciaRepository).save(novaInconsistencia);
    }

    @Test
    @DisplayName("Should throw exception when saving inconsistency without fiscal note")
    void shouldThrowExceptionWhenSavingInconsistencyWithoutFiscalNote() {
        // Given
        Inconsistencia inconsistenciaInvalida = new Inconsistencia();
        inconsistenciaInvalida.setTipo(tipoInconsistencia);
        inconsistenciaInvalida.setDescricao("Inconsistência sem nota");
        inconsistenciaInvalida.setSeveridade("ALTA");

        // When/Then
        assertThatThrownBy(() -> inconsistenciaService.salvar(inconsistenciaInvalida))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Nota fiscal é obrigatória");

        verify(inconsistenciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when saving inconsistency with non-existent fiscal note")
    void shouldThrowExceptionWhenSavingInconsistencyWithNonExistentFiscalNote() {
        // Given
        Inconsistencia inconsistenciaInvalida = new Inconsistencia();
        inconsistenciaInvalida.setNotaFiscal(notaFiscal);
        inconsistenciaInvalida.setTipo(tipoInconsistencia);
        inconsistenciaInvalida.setDescricao("Inconsistência");
        inconsistenciaInvalida.setSeveridade("ALTA");

        when(notaFiscalRepository.existsById(1L)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> inconsistenciaService.salvar(inconsistenciaInvalida))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Nota fiscal não encontrada");

        verify(notaFiscalRepository).existsById(1L);
        verify(inconsistenciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when saving inconsistency without type")
    void shouldThrowExceptionWhenSavingInconsistencyWithoutType() {
        // Given
        Inconsistencia inconsistenciaInvalida = new Inconsistencia();
        inconsistenciaInvalida.setNotaFiscal(notaFiscal);
        inconsistenciaInvalida.setDescricao("Inconsistência sem tipo");
        inconsistenciaInvalida.setSeveridade("ALTA");

        // When/Then
        assertThatThrownBy(() -> inconsistenciaService.salvar(inconsistenciaInvalida))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Tipo de inconsistência é obrigatório");

        verify(inconsistenciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when saving inconsistency without description")
    void shouldThrowExceptionWhenSavingInconsistencyWithoutDescription() {
        // Given
        Inconsistencia inconsistenciaInvalida = new Inconsistencia();
        inconsistenciaInvalida.setNotaFiscal(notaFiscal);
        inconsistenciaInvalida.setTipo(tipoInconsistencia);
        inconsistenciaInvalida.setSeveridade("ALTA");

        // When/Then
        assertThatThrownBy(() -> inconsistenciaService.salvar(inconsistenciaInvalida))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Descrição é obrigatória");

        verify(inconsistenciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when saving inconsistency without severity")
    void shouldThrowExceptionWhenSavingInconsistencyWithoutSeverity() {
        // Given
        Inconsistencia inconsistenciaInvalida = new Inconsistencia();
        inconsistenciaInvalida.setNotaFiscal(notaFiscal);
        inconsistenciaInvalida.setTipo(tipoInconsistencia);
        inconsistenciaInvalida.setDescricao("Inconsistência sem severidade");

        // When/Then
        assertThatThrownBy(() -> inconsistenciaService.salvar(inconsistenciaInvalida))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Severidade é obrigatória");

        verify(inconsistenciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when saving inconsistency with invalid severity")
    void shouldThrowExceptionWhenSavingInconsistencyWithInvalidSeverity() {
        // Given
        Inconsistencia inconsistenciaInvalida = new Inconsistencia();
        inconsistenciaInvalida.setNotaFiscal(notaFiscal);
        inconsistenciaInvalida.setTipo(tipoInconsistencia);
        inconsistenciaInvalida.setDescricao("Inconsistência");
        inconsistenciaInvalida.setSeveridade("INVALIDA");

        // When/Then
        assertThatThrownBy(() -> inconsistenciaService.salvar(inconsistenciaInvalida))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Severidade inválida");

        verify(inconsistenciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update inconsistency successfully")
    void shouldUpdateInconsistencySuccessfully() {
        // Given
        Inconsistencia inconsistenciaAtualizada = new Inconsistencia();
        inconsistenciaAtualizada.setNotaFiscal(notaFiscal);
        inconsistenciaAtualizada.setTipo(tipoInconsistencia);
        inconsistenciaAtualizada.setDescricao("Descrição atualizada");
        inconsistenciaAtualizada.setSeveridade("CRITICA");

        when(inconsistenciaRepository.findById(1L)).thenReturn(Optional.of(inconsistencia));
        when(inconsistenciaRepository.save(any(Inconsistencia.class))).thenReturn(inconsistenciaAtualizada);

        // When
        Inconsistencia result = inconsistenciaService.atualizar(1L, inconsistenciaAtualizada);

        // Then
        assertThat(result).isNotNull();
        assertThat(inconsistenciaAtualizada.getId()).isEqualTo(1L);
        assertThat(inconsistenciaAtualizada.getDataDeteccao()).isEqualTo(inconsistencia.getDataDeteccao());
        verify(inconsistenciaRepository).findById(1L);
        verify(inconsistenciaRepository).save(inconsistenciaAtualizada);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent inconsistency")
    void shouldThrowExceptionWhenUpdatingNonExistentInconsistency() {
        // Given
        Inconsistencia inconsistenciaAtualizada = new Inconsistencia();
        when(inconsistenciaRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> inconsistenciaService.atualizar(999L, inconsistenciaAtualizada))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Inconsistência não encontrada");

        verify(inconsistenciaRepository).findById(999L);
        verify(inconsistenciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete inconsistency successfully")
    void shouldDeleteInconsistencySuccessfully() {
        // Given
        when(inconsistenciaRepository.existsById(1L)).thenReturn(true);

        // When
        inconsistenciaService.deletar(1L);

        // Then
        verify(inconsistenciaRepository).existsById(1L);
        verify(inconsistenciaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent inconsistency")
    void shouldThrowExceptionWhenDeletingNonExistentInconsistency() {
        // Given
        when(inconsistenciaRepository.existsById(999L)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> inconsistenciaService.deletar(999L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Inconsistência não encontrada");

        verify(inconsistenciaRepository).existsById(999L);
        verify(inconsistenciaRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should resolve inconsistency successfully")
    void shouldResolveInconsistencySuccessfully() {
        // Given
        String observacao = "Inconsistência resolvida através de ajuste manual";
        when(inconsistenciaRepository.findById(1L)).thenReturn(Optional.of(inconsistencia));
        when(inconsistenciaRepository.save(any(Inconsistencia.class))).thenReturn(inconsistencia);

        // When
        Inconsistencia result = inconsistenciaService.resolver(1L, observacao);

        // Then
        assertThat(result).isNotNull();
        assertThat(inconsistencia.getStatus()).isEqualTo("RESOLVIDA");
        assertThat(inconsistencia.getDataResolucao()).isNotNull();
        assertThat(inconsistencia.getObservacaoResolucao()).isEqualTo(observacao);
        verify(inconsistenciaRepository).findById(1L);
        verify(inconsistenciaRepository).save(inconsistencia);
    }

    @Test
    @DisplayName("Should throw exception when resolving non-existent inconsistency")
    void shouldThrowExceptionWhenResolvingNonExistentInconsistency() {
        // Given
        when(inconsistenciaRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> inconsistenciaService.resolver(999L, "Observação"))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Inconsistência não encontrada");

        verify(inconsistenciaRepository).findById(999L);
        verify(inconsistenciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reopen inconsistency successfully")
    void shouldReopenInconsistencySuccessfully() {
        // Given
        String motivo = "Nova evidência encontrada";
        inconsistencia.setStatus("RESOLVIDA");
        inconsistencia.setDataResolucao(new Date());
        
        when(inconsistenciaRepository.findById(1L)).thenReturn(Optional.of(inconsistencia));
        when(inconsistenciaRepository.save(any(Inconsistencia.class))).thenReturn(inconsistencia);

        // When
        Inconsistencia result = inconsistenciaService.reabrir(1L, motivo);

        // Then
        assertThat(result).isNotNull();
        assertThat(inconsistencia.getStatus()).isEqualTo("REABERTA");
        assertThat(inconsistencia.getDataResolucao()).isNull();
        assertThat(inconsistencia.getObservacaoResolucao()).isEqualTo(motivo);
        verify(inconsistenciaRepository).findById(1L);
        verify(inconsistenciaRepository).save(inconsistencia);
    }

    @Test
    @DisplayName("Should throw exception when reopening non-existent inconsistency")
    void shouldThrowExceptionWhenReopeningNonExistentInconsistency() {
        // Given
        when(inconsistenciaRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> inconsistenciaService.reabrir(999L, "Motivo"))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Inconsistência não encontrada");

        verify(inconsistenciaRepository).findById(999L);
        verify(inconsistenciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should set default status when saving inconsistency without status")
    void shouldSetDefaultStatusWhenSavingInconsistencyWithoutStatus() {
        // Given
        Inconsistencia novaInconsistencia = new Inconsistencia();
        novaInconsistencia.setNotaFiscal(notaFiscal);
        novaInconsistencia.setTipo(tipoInconsistencia);
        novaInconsistencia.setDescricao("Nova inconsistência");
        novaInconsistencia.setSeveridade("BAIXA");
        novaInconsistencia.setStatus(null);

        when(notaFiscalRepository.existsById(1L)).thenReturn(true);
        when(inconsistenciaRepository.save(any(Inconsistencia.class))).thenReturn(novaInconsistencia);

        // When
        inconsistenciaService.salvar(novaInconsistencia);

        // Then
        assertThat(novaInconsistencia.getStatus()).isEqualTo("PENDENTE");
    }

    @Test
    @DisplayName("Should handle empty status when saving inconsistency")
    void shouldHandleEmptyStatusWhenSavingInconsistency() {
        // Given
        Inconsistencia novaInconsistencia = new Inconsistencia();
        novaInconsistencia.setNotaFiscal(notaFiscal);
        novaInconsistencia.setTipo(tipoInconsistencia);
        novaInconsistencia.setDescricao("Nova inconsistência");
        novaInconsistencia.setSeveridade("BAIXA");
        novaInconsistencia.setStatus("   ");

        when(notaFiscalRepository.existsById(1L)).thenReturn(true);
        when(inconsistenciaRepository.save(any(Inconsistencia.class))).thenReturn(novaInconsistencia);

        // When
        inconsistenciaService.salvar(novaInconsistencia);

        // Then
        assertThat(novaInconsistencia.getStatus()).isEqualTo("PENDENTE");
    }
}