package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.fiscal.HistoricoNotaFiscal;
import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscal;
import com.annaehugo.freepharma.domain.repository.fiscal.HistoricoNotaFiscalRepository;
import com.annaehugo.freepharma.domain.repository.fiscal.NotaFiscalRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoricoNotaFiscalServiceTest {

    @Mock
    private HistoricoNotaFiscalRepository historicoNotaFiscalRepository;

    @Mock
    private NotaFiscalRepository notaFiscalRepository;

    @InjectMocks
    private HistoricoNotaFiscalService historicoNotaFiscalService;

    private HistoricoNotaFiscal historico;
    private NotaFiscal notaFiscal;

    @BeforeEach
    void setUp() {
        notaFiscal = new NotaFiscal();
        notaFiscal.setId(1L);

        historico = new HistoricoNotaFiscal();
        historico.setId(1L);
        historico.setNotaFiscal(notaFiscal);
        historico.setTipoOperacao("CRIACAO");
        historico.setStatusAnterior("PENDENTE");
        historico.setStatusNovo("PROCESSADA");
        historico.setDataOperacao(new Date());
    }

    @Test
    void listarTodos_DeveRetornarListaDeHistoricos() {
        // Given
        List<HistoricoNotaFiscal> historicos = Arrays.asList(historico);
        when(historicoNotaFiscalRepository.findAll()).thenReturn(historicos);

        // When
        List<HistoricoNotaFiscal> resultado = historicoNotaFiscalService.listarTodos();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(historico, resultado.get(0));
        verify(historicoNotaFiscalRepository).findAll();
    }

    @Test
    void listarPorNotaFiscal_DeveRetornarHistoricosDaNota() {
        // Given
        List<HistoricoNotaFiscal> historicos = Arrays.asList(historico);
        when(historicoNotaFiscalRepository.findByNotaFiscalId(1L)).thenReturn(historicos);

        // When
        List<HistoricoNotaFiscal> resultado = historicoNotaFiscalService.listarPorNotaFiscal(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(historico, resultado.get(0));
        verify(historicoNotaFiscalRepository).findByNotaFiscalId(1L);
    }

    @Test
    void listarPorTipoOperacao_DeveRetornarHistoricosPorTipo() {
        // Given
        List<HistoricoNotaFiscal> historicos = Arrays.asList(historico);
        when(historicoNotaFiscalRepository.findByTipoOperacao("CRIACAO")).thenReturn(historicos);

        // When
        List<HistoricoNotaFiscal> resultado = historicoNotaFiscalService.listarPorTipoOperacao("CRIACAO");

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(historico, resultado.get(0));
        verify(historicoNotaFiscalRepository).findByTipoOperacao("CRIACAO");
    }

    @Test
    void buscarPorId_ComIdValido_DeveRetornarHistorico() {
        // Given
        when(historicoNotaFiscalRepository.findById(1L)).thenReturn(Optional.of(historico));

        // When
        Optional<HistoricoNotaFiscal> resultado = historicoNotaFiscalService.buscarPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(historico, resultado.get());
        verify(historicoNotaFiscalRepository).findById(1L);
    }

    @Test
    void buscarPorId_ComIdInvalido_DeveRetornarEmpty() {
        // Given
        when(historicoNotaFiscalRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<HistoricoNotaFiscal> resultado = historicoNotaFiscalService.buscarPorId(999L);

        // Then
        assertFalse(resultado.isPresent());
        verify(historicoNotaFiscalRepository).findById(999L);
    }

    @Test
    void salvar_ComDadosValidos_DeveSalvarHistorico() {
        // Given
        HistoricoNotaFiscal novoHistorico = new HistoricoNotaFiscal();
        novoHistorico.setNotaFiscal(notaFiscal);
        novoHistorico.setTipoOperacao("ATUALIZACAO");
        novoHistorico.setStatusAnterior("PROCESSADA");
        novoHistorico.setStatusNovo("FINALIZADA");

        when(notaFiscalRepository.existsById(1L)).thenReturn(true);
        when(historicoNotaFiscalRepository.save(any(HistoricoNotaFiscal.class))).thenReturn(novoHistorico);

        // When
        HistoricoNotaFiscal resultado = historicoNotaFiscalService.salvar(novoHistorico);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getDataOperacao());
        assertEquals("ATUALIZACAO", resultado.getTipoOperacao());
        verify(notaFiscalRepository).existsById(1L);
        verify(historicoNotaFiscalRepository).save(novoHistorico);
    }

    @Test
    void salvar_ComNotaFiscalInexistente_DeveLancarExcecao() {
        // Given
        HistoricoNotaFiscal historicoInvalido = new HistoricoNotaFiscal();
        historicoInvalido.setNotaFiscal(notaFiscal);
        historicoInvalido.setTipoOperacao("ATUALIZACAO");
        historicoInvalido.setStatusAnterior("PROCESSADA");
        historicoInvalido.setStatusNovo("FINALIZADA");

        when(notaFiscalRepository.existsById(1L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> historicoNotaFiscalService.salvar(historicoInvalido));
        assertEquals("Nota fiscal não encontrada", exception.getMessage());
        verify(notaFiscalRepository).existsById(1L);
        verify(historicoNotaFiscalRepository, never()).save(any());
    }

    @Test
    void salvar_SemNotaFiscal_DeveLancarExcecao() {
        // Given
        HistoricoNotaFiscal historicoInvalido = new HistoricoNotaFiscal();
        historicoInvalido.setTipoOperacao("ATUALIZACAO");
        historicoInvalido.setStatusAnterior("PROCESSADA");
        historicoInvalido.setStatusNovo("FINALIZADA");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> historicoNotaFiscalService.salvar(historicoInvalido));
        assertEquals("Nota fiscal é obrigatória", exception.getMessage());
        verify(historicoNotaFiscalRepository, never()).save(any());
    }

    @Test
    void salvar_SemTipoOperacao_DeveLancarExcecao() {
        // Given
        HistoricoNotaFiscal historicoInvalido = new HistoricoNotaFiscal();
        historicoInvalido.setNotaFiscal(notaFiscal);
        historicoInvalido.setStatusAnterior("PROCESSADA");
        historicoInvalido.setStatusNovo("FINALIZADA");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> historicoNotaFiscalService.salvar(historicoInvalido));
        assertEquals("Tipo de operação é obrigatório", exception.getMessage());
        verify(historicoNotaFiscalRepository, never()).save(any());
    }

    @Test
    void salvar_SemStatusAnterior_DeveLancarExcecao() {
        // Given
        HistoricoNotaFiscal historicoInvalido = new HistoricoNotaFiscal();
        historicoInvalido.setNotaFiscal(notaFiscal);
        historicoInvalido.setTipoOperacao("ATUALIZACAO");
        historicoInvalido.setStatusNovo("FINALIZADA");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> historicoNotaFiscalService.salvar(historicoInvalido));
        assertEquals("Status anterior é obrigatório", exception.getMessage());
        verify(historicoNotaFiscalRepository, never()).save(any());
    }

    @Test
    void salvar_SemStatusNovo_DeveLancarExcecao() {
        // Given
        HistoricoNotaFiscal historicoInvalido = new HistoricoNotaFiscal();
        historicoInvalido.setNotaFiscal(notaFiscal);
        historicoInvalido.setTipoOperacao("ATUALIZACAO");
        historicoInvalido.setStatusAnterior("PROCESSADA");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> historicoNotaFiscalService.salvar(historicoInvalido));
        assertEquals("Status novo é obrigatório", exception.getMessage());
        verify(historicoNotaFiscalRepository, never()).save(any());
    }

    @Test
    void salvar_ComTipoOperacaoVazio_DeveLancarExcecao() {
        // Given
        HistoricoNotaFiscal historicoInvalido = new HistoricoNotaFiscal();
        historicoInvalido.setNotaFiscal(notaFiscal);
        historicoInvalido.setTipoOperacao("   ");
        historicoInvalido.setStatusAnterior("PROCESSADA");
        historicoInvalido.setStatusNovo("FINALIZADA");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> historicoNotaFiscalService.salvar(historicoInvalido));
        assertEquals("Tipo de operação é obrigatório", exception.getMessage());
        verify(historicoNotaFiscalRepository, never()).save(any());
    }

    @Test
    void atualizar_ComHistoricoExistente_DeveAtualizarHistorico() {
        // Given
        HistoricoNotaFiscal historicoAtualizado = new HistoricoNotaFiscal();
        historicoAtualizado.setNotaFiscal(notaFiscal);
        historicoAtualizado.setTipoOperacao("ATUALIZACAO");
        historicoAtualizado.setStatusAnterior("PROCESSADA");
        historicoAtualizado.setStatusNovo("CANCELADA");

        when(historicoNotaFiscalRepository.findById(1L)).thenReturn(Optional.of(historico));
        when(historicoNotaFiscalRepository.save(any(HistoricoNotaFiscal.class))).thenReturn(historicoAtualizado);

        // When
        HistoricoNotaFiscal resultado = historicoNotaFiscalService.atualizar(1L, historicoAtualizado);

        // Then
        assertNotNull(resultado);
        assertEquals("CANCELADA", resultado.getStatusNovo());
        assertEquals(1L, historicoAtualizado.getId());
        verify(historicoNotaFiscalRepository).findById(1L);
        verify(historicoNotaFiscalRepository).save(historicoAtualizado);
    }

    @Test
    void atualizar_ComHistoricoInexistente_DeveLancarExcecao() {
        // Given
        HistoricoNotaFiscal historicoAtualizado = new HistoricoNotaFiscal();
        when(historicoNotaFiscalRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> historicoNotaFiscalService.atualizar(999L, historicoAtualizado));
        assertEquals("Histórico não encontrado", exception.getMessage());
        verify(historicoNotaFiscalRepository).findById(999L);
        verify(historicoNotaFiscalRepository, never()).save(any());
    }

    @Test
    void deletar_ComHistoricoExistente_DeveDeletarHistorico() {
        // Given
        when(historicoNotaFiscalRepository.existsById(1L)).thenReturn(true);

        // When
        historicoNotaFiscalService.deletar(1L);

        // Then
        verify(historicoNotaFiscalRepository).existsById(1L);
        verify(historicoNotaFiscalRepository).deleteById(1L);
    }

    @Test
    void deletar_ComHistoricoInexistente_DeveLancarExcecao() {
        // Given
        when(historicoNotaFiscalRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> historicoNotaFiscalService.deletar(999L));
        assertEquals("Histórico não encontrado", exception.getMessage());
        verify(historicoNotaFiscalRepository).existsById(999L);
        verify(historicoNotaFiscalRepository, never()).deleteById(anyLong());
    }

    @Test
    void salvar_DevdefinirDataOperacaoAutomaticamente() {
        // Given
        HistoricoNotaFiscal novoHistorico = new HistoricoNotaFiscal();
        novoHistorico.setNotaFiscal(notaFiscal);
        novoHistorico.setTipoOperacao("CRIACAO");
        novoHistorico.setStatusAnterior("NOVO");
        novoHistorico.setStatusNovo("PROCESSANDO");

        when(notaFiscalRepository.existsById(1L)).thenReturn(true);
        when(historicoNotaFiscalRepository.save(any(HistoricoNotaFiscal.class))).thenReturn(novoHistorico);

        // When
        historicoNotaFiscalService.salvar(novoHistorico);

        // Then
        assertNotNull(novoHistorico.getDataOperacao());
        verify(historicoNotaFiscalRepository).save(novoHistorico);
    }
}