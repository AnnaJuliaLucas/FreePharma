package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.fiscal.LoteProcessamento;
import com.annaehugo.freepharma.domain.repository.fiscal.LoteProcessamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoteProcessamentoServiceTest {

    @Mock
    private LoteProcessamentoRepository loteProcessamentoRepository;

    @InjectMocks
    private LoteProcessamentoService loteProcessamentoService;

    private LoteProcessamento lote;

    @BeforeEach
    void setUp() {
        lote = new LoteProcessamento();
        lote.setId(1L);
        lote.setDataInicio(new Date());
        lote.setStatus("INICIADO");
    }

    @Test
    void listarTodos_DeveRetornarListaDeLotes() {
        // Given
        List<LoteProcessamento> lotes = Arrays.asList(lote);
        when(loteProcessamentoRepository.findAll()).thenReturn(lotes);

        // When
        List<LoteProcessamento> resultado = loteProcessamentoService.listarTodos();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(lote, resultado.get(0));
        verify(loteProcessamentoRepository).findAll();
    }

    @Test
    void listarPorStatus_DeveRetornarLotesPorStatus() {
        // Given
        List<LoteProcessamento> lotes = Arrays.asList(lote);
        when(loteProcessamentoRepository.findByStatus("INICIADO")).thenReturn(lotes);

        // When
        List<LoteProcessamento> resultado = loteProcessamentoService.listarPorStatus("INICIADO");

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(lote, resultado.get(0));
        verify(loteProcessamentoRepository).findByStatus("INICIADO");
    }

    @Test
    void buscarPorId_ComIdValido_DeveRetornarLote() {
        // Given
        when(loteProcessamentoRepository.findById(1L)).thenReturn(Optional.of(lote));

        // When
        Optional<LoteProcessamento> resultado = loteProcessamentoService.buscarPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(lote, resultado.get());
        verify(loteProcessamentoRepository).findById(1L);
    }

    @Test
    void buscarPorId_ComIdInvalido_DeveRetornarEmpty() {
        // Given
        when(loteProcessamentoRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<LoteProcessamento> resultado = loteProcessamentoService.buscarPorId(999L);

        // Then
        assertFalse(resultado.isPresent());
        verify(loteProcessamentoRepository).findById(999L);
    }

    @Test
    void salvar_ComLoteCompleto_DeveSalvarLote() {
        // Given
        LoteProcessamento novoLote = new LoteProcessamento();
        novoLote.setDataInicio(new Date());
        novoLote.setStatus("PROCESSANDO");

        when(loteProcessamentoRepository.save(any(LoteProcessamento.class))).thenReturn(novoLote);

        // When
        LoteProcessamento resultado = loteProcessamentoService.salvar(novoLote);

        // Then
        assertNotNull(resultado);
        assertEquals("PROCESSANDO", resultado.getStatus());
        verify(loteProcessamentoRepository).save(novoLote);
    }

    @Test
    void salvar_SemDataInicio_DeveDefinirDataInicioAutomaticamente() {
        // Given
        LoteProcessamento novoLote = new LoteProcessamento();
        
        when(loteProcessamentoRepository.save(any(LoteProcessamento.class))).thenReturn(novoLote);

        // When
        loteProcessamentoService.salvar(novoLote);

        // Then
        assertNotNull(novoLote.getDataInicio());
        verify(loteProcessamentoRepository).save(novoLote);
    }

    @Test
    void salvar_SemStatus_DeveDefinirStatusIniciadoAutomaticamente() {
        // Given
        LoteProcessamento novoLote = new LoteProcessamento();
        
        when(loteProcessamentoRepository.save(any(LoteProcessamento.class))).thenReturn(novoLote);

        // When
        loteProcessamentoService.salvar(novoLote);

        // Then
        assertEquals("INICIADO", novoLote.getStatus());
        verify(loteProcessamentoRepository).save(novoLote);
    }

    @Test
    void salvar_ComDataInicioExistente_NaoDeveAlterarDataInicio() {
        // Given
        Date dataInicioEspecifica = new Date(System.currentTimeMillis() - 3600000); // 1 hora atrás
        LoteProcessamento novoLote = new LoteProcessamento();
        novoLote.setDataInicio(dataInicioEspecifica);
        
        when(loteProcessamentoRepository.save(any(LoteProcessamento.class))).thenReturn(novoLote);

        // When
        loteProcessamentoService.salvar(novoLote);

        // Then
        assertEquals(dataInicioEspecifica, novoLote.getDataInicio());
        verify(loteProcessamentoRepository).save(novoLote);
    }

    @Test
    void salvar_ComStatusExistente_NaoDeveAlterarStatus() {
        // Given
        LoteProcessamento novoLote = new LoteProcessamento();
        novoLote.setStatus("PROCESSANDO");
        
        when(loteProcessamentoRepository.save(any(LoteProcessamento.class))).thenReturn(novoLote);

        // When
        loteProcessamentoService.salvar(novoLote);

        // Then
        assertEquals("PROCESSANDO", novoLote.getStatus());
        verify(loteProcessamentoRepository).save(novoLote);
    }

    @Test
    void finalizar_ComLoteExistente_DeveFinalizarLote() {
        // Given
        when(loteProcessamentoRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(loteProcessamentoRepository.save(any(LoteProcessamento.class))).thenReturn(lote);

        // When
        LoteProcessamento resultado = loteProcessamentoService.finalizar(1L, "CONCLUIDO");

        // Then
        assertNotNull(resultado);
        assertEquals("CONCLUIDO", lote.getStatus());
        assertNotNull(lote.getDataFim());
        verify(loteProcessamentoRepository).findById(1L);
        verify(loteProcessamentoRepository).save(lote);
    }

    @Test
    void finalizar_ComLoteInexistente_DeveLancarExcecao() {
        // Given
        when(loteProcessamentoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> loteProcessamentoService.finalizar(999L, "CONCLUIDO"));
        assertEquals("Lote não encontrado", exception.getMessage());
        verify(loteProcessamentoRepository).findById(999L);
        verify(loteProcessamentoRepository, never()).save(any());
    }

    @Test
    void finalizar_ComStatusErro_DeveFinalizarComErro() {
        // Given
        when(loteProcessamentoRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(loteProcessamentoRepository.save(any(LoteProcessamento.class))).thenReturn(lote);

        // When
        LoteProcessamento resultado = loteProcessamentoService.finalizar(1L, "ERRO");

        // Then
        assertNotNull(resultado);
        assertEquals("ERRO", lote.getStatus());
        assertNotNull(lote.getDataFim());
        verify(loteProcessamentoRepository).findById(1L);
        verify(loteProcessamentoRepository).save(lote);
    }

    @Test
    void finalizar_ComStatusCancelado_DeveFinalizarComCancelado() {
        // Given
        when(loteProcessamentoRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(loteProcessamentoRepository.save(any(LoteProcessamento.class))).thenReturn(lote);

        // When
        LoteProcessamento resultado = loteProcessamentoService.finalizar(1L, "CANCELADO");

        // Then
        assertNotNull(resultado);
        assertEquals("CANCELADO", lote.getStatus());
        assertNotNull(lote.getDataFim());
        verify(loteProcessamentoRepository).findById(1L);
        verify(loteProcessamentoRepository).save(lote);
    }

    @Test
    void listarPorStatus_ComStatusInexistente_DeveRetornarListaVazia() {
        // Given
        when(loteProcessamentoRepository.findByStatus("STATUS_INEXISTENTE")).thenReturn(Arrays.asList());

        // When
        List<LoteProcessamento> resultado = loteProcessamentoService.listarPorStatus("STATUS_INEXISTENTE");

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(loteProcessamentoRepository).findByStatus("STATUS_INEXISTENTE");
    }

    @Test
    void finalizar_DeveDefinirDataFimCorretamente() {
        // Given
        Date dataAntes = new Date();
        when(loteProcessamentoRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(loteProcessamentoRepository.save(any(LoteProcessamento.class))).thenReturn(lote);

        // When
        loteProcessamentoService.finalizar(1L, "CONCLUIDO");
        Date dataDepois = new Date();

        // Then
        assertNotNull(lote.getDataFim());
        assertTrue(lote.getDataFim().getTime() >= dataAntes.getTime());
        assertTrue(lote.getDataFim().getTime() <= dataDepois.getTime());
    }
}