package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscal;
import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscalItem;
import com.annaehugo.freepharma.domain.entity.fiscal.Inconsistencia;
import com.annaehugo.freepharma.domain.repository.fiscal.NotaFiscalRepository;
import com.annaehugo.freepharma.domain.repository.fiscal.NotaFiscalItemRepository;
import com.annaehugo.freepharma.domain.repository.fiscal.InconsistenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotaFiscalServiceTest {

    @Mock
    private NotaFiscalRepository notaFiscalRepository;

    @Mock
    private NotaFiscalItemRepository notaFiscalItemRepository;

    @Mock
    private InconsistenciaRepository inconsistenciaRepository;

    @InjectMocks
    private NotaFiscalService notaFiscalService;

    private NotaFiscal notaFiscal;
    private NotaFiscalItem item;
    private Inconsistencia inconsistencia;
    private Date dataInicio;
    private Date dataFim;

    @BeforeEach
    void setUp() {
        notaFiscal = new NotaFiscal();
        notaFiscal.setId(1L);
        notaFiscal.setNumero("12345");
        notaFiscal.setChaveAcesso("35200714200166000187550010000004421375948936");
        notaFiscal.setStatus("PROCESSADA");
        notaFiscal.setTipoOperacao("ENTRADA");
        
        item = new NotaFiscalItem();
        item.setId(1L);
        
        inconsistencia = new Inconsistencia();
        inconsistencia.setId(1L);
        
        dataInicio = new Date(System.currentTimeMillis() - 86400000); // 1 dia atrás
        dataFim = new Date();
    }

    @Test
    void listarTodas_DeveRetornarListaDeNotasFiscais() {
        // Given
        List<NotaFiscal> notasFiscais = Arrays.asList(notaFiscal);
        when(notaFiscalRepository.findAll()).thenReturn(notasFiscais);

        // When
        List<NotaFiscal> resultado = notaFiscalService.listarTodas();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(notaFiscal, resultado.get(0));
        verify(notaFiscalRepository).findAll();
    }

    @Test
    void listarComPaginacao_DeveRetornarPaginaDeNotasFiscais() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<NotaFiscal> notasFiscais = Arrays.asList(notaFiscal);
        Page<NotaFiscal> page = new PageImpl<>(notasFiscais, pageable, 1);
        when(notaFiscalRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<NotaFiscal> resultado = notaFiscalService.listarComPaginacao(pageable);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals(notaFiscal, resultado.getContent().get(0));
        verify(notaFiscalRepository).findAll(pageable);
    }

    @Test
    void listarPorUnidade_DeveRetornarNotasFiscaisDaUnidade() {
        // Given
        List<NotaFiscal> notasFiscais = Arrays.asList(notaFiscal);
        when(notaFiscalRepository.findByUnidadeId(1L)).thenReturn(notasFiscais);

        // When
        List<NotaFiscal> resultado = notaFiscalService.listarPorUnidade(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(notaFiscal, resultado.get(0));
        verify(notaFiscalRepository).findByUnidadeId(1L);
    }

    @Test
    void listarPorStatus_DeveRetornarNotasFiscaisPorStatus() {
        // Given
        List<NotaFiscal> notasFiscais = Arrays.asList(notaFiscal);
        when(notaFiscalRepository.findByStatus("PROCESSADA")).thenReturn(notasFiscais);

        // When
        List<NotaFiscal> resultado = notaFiscalService.listarPorStatus("PROCESSADA");

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(notaFiscal, resultado.get(0));
        verify(notaFiscalRepository).findByStatus("PROCESSADA");
    }

    @Test
    void listarPorTipoOperacao_DeveRetornarNotasFiscaisPorTipo() {
        // Given
        List<NotaFiscal> notasFiscais = Arrays.asList(notaFiscal);
        when(notaFiscalRepository.findByTipoOperacao("ENTRADA")).thenReturn(notasFiscais);

        // When
        List<NotaFiscal> resultado = notaFiscalService.listarPorTipoOperacao("ENTRADA");

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(notaFiscal, resultado.get(0));
        verify(notaFiscalRepository).findByTipoOperacao("ENTRADA");
    }

    @Test
    void listarPorPeriodo_DeveRetornarNotasFiscaisNoPeriodo() {
        // Given
        List<NotaFiscal> notasFiscais = Arrays.asList(notaFiscal);
        when(notaFiscalRepository.findByDataEmissaoBetween(dataInicio, dataFim)).thenReturn(notasFiscais);

        // When
        List<NotaFiscal> resultado = notaFiscalService.listarPorPeriodo(dataInicio, dataFim);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(notaFiscal, resultado.get(0));
        verify(notaFiscalRepository).findByDataEmissaoBetween(dataInicio, dataFim);
    }

    @Test
    void buscarPorId_ComIdValido_DeveRetornarNotaFiscal() {
        // Given
        when(notaFiscalRepository.findById(1L)).thenReturn(Optional.of(notaFiscal));

        // When
        Optional<NotaFiscal> resultado = notaFiscalService.buscarPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(notaFiscal, resultado.get());
        verify(notaFiscalRepository).findById(1L);
    }

    @Test
    void buscarPorId_ComIdInvalido_DeveRetornarEmpty() {
        // Given
        when(notaFiscalRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<NotaFiscal> resultado = notaFiscalService.buscarPorId(999L);

        // Then
        assertFalse(resultado.isPresent());
        verify(notaFiscalRepository).findById(999L);
    }

    @Test
    void buscarPorChaveAcesso_ComChaveValida_DeveRetornarNotaFiscal() {
        // Given
        String chaveAcesso = "35200714200166000187550010000004421375948936";
        when(notaFiscalRepository.findByChaveAcesso(chaveAcesso)).thenReturn(Optional.of(notaFiscal));

        // When
        Optional<NotaFiscal> resultado = notaFiscalService.buscarPorChaveAcesso(chaveAcesso);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(notaFiscal, resultado.get());
        verify(notaFiscalRepository).findByChaveAcesso(chaveAcesso);
    }

    @Test
    void buscarPorNumero_ComNumeroValido_DeveRetornarNotaFiscal() {
        // Given
        when(notaFiscalRepository.findByNumero("12345")).thenReturn(Optional.of(notaFiscal));

        // When
        Optional<NotaFiscal> resultado = notaFiscalService.buscarPorNumero("12345");

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(notaFiscal, resultado.get());
        verify(notaFiscalRepository).findByNumero("12345");
    }

    @Test
    void listarItensPorNota_DeveRetornarItensDeUmaNota() {
        // Given
        List<NotaFiscalItem> itens = Arrays.asList(item);
        when(notaFiscalItemRepository.findByNotaFiscalId(1L)).thenReturn(itens);

        // When
        List<NotaFiscalItem> resultado = notaFiscalService.listarItensPorNota(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(item, resultado.get(0));
        verify(notaFiscalItemRepository).findByNotaFiscalId(1L);
    }

    @Test
    void listarInconsistenciasPorNota_DeveRetornarInconsistenciasDeUmaNota() {
        // Given
        List<Inconsistencia> inconsistencias = Arrays.asList(inconsistencia);
        when(inconsistenciaRepository.findByNotaFiscalId(1L)).thenReturn(inconsistencias);

        // When
        List<Inconsistencia> resultado = notaFiscalService.listarInconsistenciasPorNota(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(inconsistencia, resultado.get(0));
        verify(inconsistenciaRepository).findByNotaFiscalId(1L);
    }

    @Test
    void buscarPorFornecedor_DeveRetornarNotasFiscaisDoFornecedor() {
        // Given
        List<NotaFiscal> notasFiscais = Arrays.asList(notaFiscal);
        when(notaFiscalRepository.findByFornecedorId(1L)).thenReturn(notasFiscais);

        // When
        List<NotaFiscal> resultado = notaFiscalService.buscarPorFornecedor(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(notaFiscal, resultado.get(0));
        verify(notaFiscalRepository).findByFornecedorId(1L);
    }

    @Test
    void buscarPorCliente_DeveRetornarNotasFiscaisDoCliente() {
        // Given
        List<NotaFiscal> notasFiscais = Arrays.asList(notaFiscal);
        when(notaFiscalRepository.findByClienteId(1L)).thenReturn(notasFiscais);

        // When
        List<NotaFiscal> resultado = notaFiscalService.buscarPorCliente(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(notaFiscal, resultado.get(0));
        verify(notaFiscalRepository).findByClienteId(1L);
    }

    @Test
    void contarNotasPorPeriodo_DeveRetornarQuantidadeDeNotasNoPeriodo() {
        // Given
        when(notaFiscalRepository.countByDataEmissaoBetween(dataInicio, dataFim)).thenReturn(5L);

        // When
        Long resultado = notaFiscalService.contarNotasPorPeriodo(dataInicio, dataFim);

        // Then
        assertEquals(5L, resultado);
        verify(notaFiscalRepository).countByDataEmissaoBetween(dataInicio, dataFim);
    }

    @Test
    void buscarPorChaveAcesso_ComChaveInvalida_DeveRetornarEmpty() {
        // Given
        String chaveInvalida = "chave-inexistente";
        when(notaFiscalRepository.findByChaveAcesso(chaveInvalida)).thenReturn(Optional.empty());

        // When
        Optional<NotaFiscal> resultado = notaFiscalService.buscarPorChaveAcesso(chaveInvalida);

        // Then
        assertFalse(resultado.isPresent());
        verify(notaFiscalRepository).findByChaveAcesso(chaveInvalida);
    }

    @Test
    void buscarPorNumero_ComNumeroInvalido_DeveRetornarEmpty() {
        // Given
        String numeroInvalido = "999999";
        when(notaFiscalRepository.findByNumero(numeroInvalido)).thenReturn(Optional.empty());

        // When
        Optional<NotaFiscal> resultado = notaFiscalService.buscarPorNumero(numeroInvalido);

        // Then
        assertFalse(resultado.isPresent());
        verify(notaFiscalRepository).findByNumero(numeroInvalido);
    }

    @Test
    void listarItensPorNota_ComNotaInexistente_DeveRetornarListaVazia() {
        // Given
        when(notaFiscalItemRepository.findByNotaFiscalId(999L)).thenReturn(Arrays.asList());

        // When
        List<NotaFiscalItem> resultado = notaFiscalService.listarItensPorNota(999L);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(notaFiscalItemRepository).findByNotaFiscalId(999L);
    }

    @Test
    void listarInconsistenciasPorNota_ComNotaInexistente_DeveRetornarListaVazia() {
        // Given
        when(inconsistenciaRepository.findByNotaFiscalId(999L)).thenReturn(Arrays.asList());

        // When
        List<Inconsistencia> resultado = notaFiscalService.listarInconsistenciasPorNota(999L);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(inconsistenciaRepository).findByNotaFiscalId(999L);
    }
}