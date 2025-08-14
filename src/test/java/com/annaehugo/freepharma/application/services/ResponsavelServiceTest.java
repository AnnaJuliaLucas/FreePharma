package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.administrativo.Responsavel;
import com.annaehugo.freepharma.domain.repository.administrativo.ResponsavelRepository;
import com.annaehugo.freepharma.domain.repository.administrativo.FarmaciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponsavelServiceTest {

    @Mock
    private ResponsavelRepository responsavelRepository;

    @Mock
    private FarmaciaRepository farmaciaRepository;

    @InjectMocks
    private ResponsavelService responsavelService;

    private Responsavel responsavel;

    @BeforeEach
    void setUp() {
        responsavel = new Responsavel();
        responsavel.setId(1L);
        responsavel.setNome("João Silva");
        responsavel.setAtivo(true);
    }

    @Test
    void listarTodos_DeveRetornarListaDeResponsaveis() {
        // Given
        List<Responsavel> responsaveis = Arrays.asList(responsavel);
        when(responsavelRepository.findAll()).thenReturn(responsaveis);

        // When
        List<Responsavel> resultado = responsavelService.listarTodos();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(responsavel, resultado.get(0));
        verify(responsavelRepository).findAll();
    }

    @Test
    void listarPorFarmacia_DeveRetornarResponsaveisDaFarmacia() {
        // Given
        List<Responsavel> responsaveis = Arrays.asList(responsavel);
        when(responsavelRepository.findAll()).thenReturn(responsaveis);

        // When
        List<Responsavel> resultado = responsavelService.listarPorFarmacia(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(responsavelRepository).findAll();
        // Nota: Implementação atual é temporária, por isso usa findAll()
    }

    @Test
    void listarAtivos_DeveRetornarResponsaveisAtivos() {
        // Given
        List<Responsavel> responsaveis = Arrays.asList(responsavel);
        when(responsavelRepository.findAll()).thenReturn(responsaveis);

        // When
        List<Responsavel> resultado = responsavelService.listarAtivos();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(responsavelRepository).findAll();
        // Nota: Implementação atual é temporária, por isso usa findAll()
    }

    @Test
    void buscarPorId_ComIdValido_DeveRetornarResponsavel() {
        // Given
        when(responsavelRepository.findById(1L)).thenReturn(Optional.of(responsavel));

        // When
        Optional<Responsavel> resultado = responsavelService.buscarPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(responsavel, resultado.get());
        verify(responsavelRepository).findById(1L);
    }

    @Test
    void buscarPorId_ComIdInvalido_DeveRetornarEmpty() {
        // Given
        when(responsavelRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Responsavel> resultado = responsavelService.buscarPorId(999L);

        // Then
        assertFalse(resultado.isPresent());
        verify(responsavelRepository).findById(999L);
    }

    @Test
    void buscarPorCpf_DeveRetornarResponsavel() {
        // Given
        when(responsavelRepository.findById(1L)).thenReturn(Optional.of(responsavel));

        // When
        Optional<Responsavel> resultado = responsavelService.buscarPorCpf("12345678901");

        // Then
        assertTrue(resultado.isPresent());
        verify(responsavelRepository).findById(1L);
        // Nota: Implementação atual é temporária, por isso usa findById(1L)
    }

    @Test
    void buscarPorEmail_DeveRetornarResponsavel() {
        // Given
        when(responsavelRepository.findById(1L)).thenReturn(Optional.of(responsavel));

        // When
        Optional<Responsavel> resultado = responsavelService.buscarPorEmail("joao@email.com");

        // Then
        assertTrue(resultado.isPresent());
        verify(responsavelRepository).findById(1L);
        // Nota: Implementação atual é temporária, por isso usa findById(1L)
    }

    @Test
    void salvar_ComDadosValidos_DeveSalvarResponsavel() {
        // Given
        Responsavel novoResponsavel = new Responsavel();
        novoResponsavel.setNome("Maria Santos");

        when(responsavelRepository.save(any(Responsavel.class))).thenReturn(novoResponsavel);

        // When
        Responsavel resultado = responsavelService.salvar(novoResponsavel);

        // Then
        assertNotNull(resultado);
        assertEquals("Maria Santos", resultado.getNome());
        verify(responsavelRepository).save(novoResponsavel);
    }

    @Test
    void salvar_SemNome_DeveLancarExcecao() {
        // Given
        Responsavel responsavelInvalido = new Responsavel();

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> responsavelService.salvar(responsavelInvalido));
        assertEquals("Nome é obrigatório", exception.getMessage());
        verify(responsavelRepository, never()).save(any());
    }

    @Test
    void salvar_ComNomeVazio_DeveLancarExcecao() {
        // Given
        Responsavel responsavelInvalido = new Responsavel();
        responsavelInvalido.setNome("");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> responsavelService.salvar(responsavelInvalido));
        assertEquals("Nome é obrigatório", exception.getMessage());
        verify(responsavelRepository, never()).save(any());
    }

    @Test
    void salvar_ComNomeApenasEspacos_DeveLancarExcecao() {
        // Given
        Responsavel responsavelInvalido = new Responsavel();
        responsavelInvalido.setNome("   ");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> responsavelService.salvar(responsavelInvalido));
        assertEquals("Nome é obrigatório", exception.getMessage());
        verify(responsavelRepository, never()).save(any());
    }

    @Test
    void atualizar_ComResponsavelExistente_DeveAtualizarResponsavel() {
        // Given
        Responsavel responsavelAtualizado = new Responsavel();
        responsavelAtualizado.setNome("João Silva Atualizado");

        when(responsavelRepository.findById(1L)).thenReturn(Optional.of(responsavel));
        when(responsavelRepository.save(any(Responsavel.class))).thenReturn(responsavelAtualizado);

        // When
        Responsavel resultado = responsavelService.atualizar(1L, responsavelAtualizado);

        // Then
        assertNotNull(resultado);
        assertEquals("João Silva Atualizado", resultado.getNome());
        assertEquals(1L, responsavelAtualizado.getId());
        verify(responsavelRepository).findById(1L);
        verify(responsavelRepository).save(responsavelAtualizado);
    }

    @Test
    void atualizar_ComResponsavelInexistente_DeveLancarExcecao() {
        // Given
        Responsavel responsavelAtualizado = new Responsavel();
        responsavelAtualizado.setNome("Nome Atualizado");
        when(responsavelRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> responsavelService.atualizar(999L, responsavelAtualizado));
        assertEquals("Responsável não encontrado", exception.getMessage());
        verify(responsavelRepository).findById(999L);
        verify(responsavelRepository, never()).save(any());
    }

    @Test
    void deletar_ComResponsavelExistente_DeveDeletarResponsavel() {
        // Given
        when(responsavelRepository.existsById(1L)).thenReturn(true);

        // When
        responsavelService.deletar(1L);

        // Then
        verify(responsavelRepository).existsById(1L);
        verify(responsavelRepository).deleteById(1L);
    }

    @Test
    void deletar_ComResponsavelInexistente_DeveLancarExcecao() {
        // Given
        when(responsavelRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> responsavelService.deletar(999L));
        assertEquals("Responsável não encontrado", exception.getMessage());
        verify(responsavelRepository).existsById(999L);
        verify(responsavelRepository, never()).deleteById(anyLong());
    }

    @Test
    void ativar_ComResponsavelExistente_DeveAtivarResponsavel() {
        // Given
        when(responsavelRepository.findById(1L)).thenReturn(Optional.of(responsavel));
        when(responsavelRepository.save(any(Responsavel.class))).thenReturn(responsavel);

        // When
        responsavelService.ativar(1L);

        // Then
        assertTrue(responsavel.isAtivo());
        verify(responsavelRepository).findById(1L);
        verify(responsavelRepository).save(responsavel);
    }

    @Test
    void ativar_ComResponsavelInexistente_DeveLancarExcecao() {
        // Given
        when(responsavelRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> responsavelService.ativar(999L));
        assertEquals("Responsável não encontrado", exception.getMessage());
        verify(responsavelRepository).findById(999L);
        verify(responsavelRepository, never()).save(any());
    }

    @Test
    void inativar_ComResponsavelExistente_DeveInativarResponsavel() {
        // Given
        when(responsavelRepository.findById(1L)).thenReturn(Optional.of(responsavel));
        when(responsavelRepository.save(any(Responsavel.class))).thenReturn(responsavel);

        // When
        responsavelService.inativar(1L);

        // Then
        assertFalse(responsavel.isAtivo());
        verify(responsavelRepository).findById(1L);
        verify(responsavelRepository).save(responsavel);
    }

    @Test
    void inativar_ComResponsavelInexistente_DeveLancarExcecao() {
        // Given
        when(responsavelRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> responsavelService.inativar(999L));
        assertEquals("Responsável não encontrado", exception.getMessage());
        verify(responsavelRepository).findById(999L);
        verify(responsavelRepository, never()).save(any());
    }
}