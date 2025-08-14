package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.administrativo.Farmacia;
import com.annaehugo.freepharma.domain.repository.administrativo.FarmaciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FarmaciaService Tests")
class FarmaciaServiceTest {

    @Mock
    private FarmaciaRepository farmaciaRepository;

    private FarmaciaService farmaciaService;

    private Farmacia farmacia;

    @BeforeEach
    void setUp() {
        farmaciaService = new FarmaciaService(farmaciaRepository);

        farmacia = new Farmacia();
        farmacia.setId(1L);
        farmacia.setRazaoSocial("Farmácia Teste Ltda");
        farmacia.setNomeFantasia("Farmácia Teste");
        farmacia.setCnpj("12.345.678/0001-90");
        farmacia.setInscricaoEstadual("123456789");
        farmacia.setEmailContato("contato@farmaciateste.com.br");
        farmacia.setTelefoneContato("(11) 99999-9999");
        farmacia.setStatus("ATIVA");
        farmacia.setAtivo(true);
    }

    @Test
    @DisplayName("Should list all farmacias successfully")
    void shouldListAllFarmaciasSuccessfully() {
        // Given
        List<Farmacia> farmacias = Arrays.asList(farmacia, new Farmacia());
        when(farmaciaRepository.findAll()).thenReturn(farmacias);

        // When
        List<Farmacia> result = farmaciaService.listarTodas();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(farmacia);
        verify(farmaciaRepository).findAll();
    }

    @Test
    @DisplayName("Should find farmacia by id successfully")
    void shouldFindFarmaciaByIdSuccessfully() {
        // Given
        Long id = 1L;
        when(farmaciaRepository.findById(id)).thenReturn(Optional.of(farmacia));

        // When
        Optional<Farmacia> result = farmaciaService.buscarPorId(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(farmacia);
        verify(farmaciaRepository).findById(id);
    }

    @Test
    @DisplayName("Should return empty when farmacia not found by id")
    void shouldReturnEmptyWhenFarmaciaNotFoundById() {
        // Given
        Long id = 999L;
        when(farmaciaRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Farmacia> result = farmaciaService.buscarPorId(id);

        // Then
        assertThat(result).isEmpty();
        verify(farmaciaRepository).findById(id);
    }

    @Test
    @DisplayName("Should find farmacia by CNPJ successfully")
    void shouldFindFarmaciaByCnpjSuccessfully() {
        // Given
        String cnpj = "12.345.678/0001-90";
        when(farmaciaRepository.findByCnpj(cnpj)).thenReturn(Optional.of(farmacia));

        // When
        Optional<Farmacia> result = farmaciaService.buscarPorCnpj(cnpj);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(farmacia);
        verify(farmaciaRepository).findByCnpj(cnpj);
    }

    @Test
    @DisplayName("Should find farmacias by status successfully")
    void shouldFindFarmaciasByStatusSuccessfully() {
        // Given
        String status = "ATIVA";
        List<Farmacia> farmacias = Arrays.asList(farmacia);
        when(farmaciaRepository.findByStatus(status)).thenReturn(farmacias);

        // When
        List<Farmacia> result = farmaciaService.buscarPorStatus(status);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(farmacia);
        verify(farmaciaRepository).findByStatus(status);
    }

    @Test
    @DisplayName("Should save new farmacia successfully")
    void shouldSaveNewFarmaciaSuccessfully() {
        // Given
        Farmacia novaFarmacia = new Farmacia();
        novaFarmacia.setRazaoSocial("Nova Farmácia");
        novaFarmacia.setCnpj("98.765.432/0001-10");
        novaFarmacia.setEmailContato("nova@farmacia.com");

        when(farmaciaRepository.findByCnpj(novaFarmacia.getCnpj())).thenReturn(Optional.empty());
        when(farmaciaRepository.save(novaFarmacia)).thenReturn(novaFarmacia);

        // When
        Farmacia result = farmaciaService.salvar(novaFarmacia);

        // Then
        assertThat(result).isEqualTo(novaFarmacia);
        verify(farmaciaRepository).findByCnpj(novaFarmacia.getCnpj());
        verify(farmaciaRepository).save(novaFarmacia);
    }

    @Test
    @DisplayName("Should throw exception when saving farmacia with duplicate CNPJ")
    void shouldThrowExceptionWhenSavingFarmaciaWithDuplicateCnpj() {
        // Given
        Farmacia novaFarmacia = new Farmacia();
        novaFarmacia.setRazaoSocial("Nova Farmácia");
        novaFarmacia.setCnpj("12.345.678/0001-90");

        when(farmaciaRepository.findByCnpj(novaFarmacia.getCnpj())).thenReturn(Optional.of(farmacia));

        // When/Then
        assertThatThrownBy(() -> farmaciaService.salvar(novaFarmacia))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("CNPJ já cadastrado para outra farmácia");

        verify(farmaciaRepository).findByCnpj(novaFarmacia.getCnpj());
        verify(farmaciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when saving farmacia without razao social")
    void shouldThrowExceptionWhenSavingFarmaciaWithoutRazaoSocial() {
        // Given
        Farmacia farmaciaInvalida = new Farmacia();
        farmaciaInvalida.setRazaoSocial("");
        farmaciaInvalida.setCnpj("12.345.678/0001-90");

        // When/Then
        assertThatThrownBy(() -> farmaciaService.salvar(farmaciaInvalida))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Razão social é obrigatória");

        verify(farmaciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when saving farmacia without CNPJ")
    void shouldThrowExceptionWhenSavingFarmaciaWithoutCnpj() {
        // Given
        Farmacia farmaciaInvalida = new Farmacia();
        farmaciaInvalida.setRazaoSocial("Farmácia Teste");
        farmaciaInvalida.setCnpj("");

        // When/Then
        assertThatThrownBy(() -> farmaciaService.salvar(farmaciaInvalida))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("CNPJ é obrigatório");

        verify(farmaciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when saving farmacia with invalid CNPJ")
    void shouldThrowExceptionWhenSavingFarmaciaWithInvalidCnpj() {
        // Given
        Farmacia farmaciaInvalida = new Farmacia();
        farmaciaInvalida.setRazaoSocial("Farmácia Teste");
        farmaciaInvalida.setCnpj("123456789");

        // When/Then
        assertThatThrownBy(() -> farmaciaService.salvar(farmaciaInvalida))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("CNPJ inválido");

        verify(farmaciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when saving farmacia with invalid email")
    void shouldThrowExceptionWhenSavingFarmaciaWithInvalidEmail() {
        // Given
        Farmacia farmaciaInvalida = new Farmacia();
        farmaciaInvalida.setRazaoSocial("Farmácia Teste");
        farmaciaInvalida.setCnpj("12.345.678/0001-90");
        farmaciaInvalida.setEmailContato("email-invalido");

        // When/Then
        assertThatThrownBy(() -> farmaciaService.salvar(farmaciaInvalida))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Email inválido");

        verify(farmaciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update farmacia successfully")
    void shouldUpdateFarmaciaSuccessfully() {
        // Given
        Long id = 1L;
        Farmacia farmaciaAtualizada = new Farmacia();
        farmaciaAtualizada.setRazaoSocial("Farmácia Atualizada");
        farmaciaAtualizada.setCnpj("98.765.432/0001-10");
        farmaciaAtualizada.setEmailContato("atualizada@farmacia.com");

        when(farmaciaRepository.findById(id)).thenReturn(Optional.of(farmacia));
        when(farmaciaRepository.save(any(Farmacia.class))).thenReturn(farmaciaAtualizada);

        // When
        Farmacia result = farmaciaService.atualizar(id, farmaciaAtualizada);

        // Then
        assertThat(result).isEqualTo(farmaciaAtualizada);
        assertThat(farmaciaAtualizada.getId()).isEqualTo(id);
        assertThat(farmaciaAtualizada.getCnpj()).isEqualTo(farmacia.getCnpj()); // CNPJ não deve ser alterado

        verify(farmaciaRepository).findById(id);
        verify(farmaciaRepository).save(farmaciaAtualizada);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent farmacia")
    void shouldThrowExceptionWhenUpdatingNonExistentFarmacia() {
        // Given
        Long id = 999L;
        Farmacia farmaciaAtualizada = new Farmacia();

        when(farmaciaRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> farmaciaService.atualizar(id, farmaciaAtualizada))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Farmácia não encontrada");

        verify(farmaciaRepository).findById(id);
        verify(farmaciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete farmacia successfully")
    void shouldDeleteFarmaciaSuccessfully() {
        // Given
        Long id = 1L;
        when(farmaciaRepository.existsById(id)).thenReturn(true);

        // When
        farmaciaService.deletar(id);

        // Then
        verify(farmaciaRepository).existsById(id);
        verify(farmaciaRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent farmacia")
    void shouldThrowExceptionWhenDeletingNonExistentFarmacia() {
        // Given
        Long id = 999L;
        when(farmaciaRepository.existsById(id)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> farmaciaService.deletar(id))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Farmácia não encontrada");

        verify(farmaciaRepository).existsById(id);
        verify(farmaciaRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should activate farmacia successfully")
    void shouldActivateFarmaciaSuccessfully() {
        // Given
        Long id = 1L;
        farmacia.setStatus("INATIVA");

        when(farmaciaRepository.findById(id)).thenReturn(Optional.of(farmacia));
        when(farmaciaRepository.save(farmacia)).thenReturn(farmacia);

        // When
        farmaciaService.ativar(id);

        // Then
        assertThat(farmacia.getStatus()).isEqualTo("ATIVA");
        verify(farmaciaRepository).findById(id);
        verify(farmaciaRepository).save(farmacia);
    }

    @Test
    @DisplayName("Should throw exception when activating non-existent farmacia")
    void shouldThrowExceptionWhenActivatingNonExistentFarmacia() {
        // Given
        Long id = 999L;
        when(farmaciaRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> farmaciaService.ativar(id))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Farmácia não encontrada");

        verify(farmaciaRepository).findById(id);
        verify(farmaciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should deactivate farmacia successfully")
    void shouldDeactivateFarmaciaSuccessfully() {
        // Given
        Long id = 1L;
        farmacia.setStatus("ATIVA");

        when(farmaciaRepository.findById(id)).thenReturn(Optional.of(farmacia));
        when(farmaciaRepository.save(farmacia)).thenReturn(farmacia);

        // When
        farmaciaService.inativar(id);

        // Then
        assertThat(farmacia.getStatus()).isEqualTo("INATIVA");
        verify(farmaciaRepository).findById(id);
        verify(farmaciaRepository).save(farmacia);
    }

    @Test
    @DisplayName("Should throw exception when deactivating non-existent farmacia")
    void shouldThrowExceptionWhenDeactivatingNonExistentFarmacia() {
        // Given
        Long id = 999L;
        when(farmaciaRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> farmaciaService.inativar(id))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Farmácia não encontrada");

        verify(farmaciaRepository).findById(id);
        verify(farmaciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should save farmacia with valid CNPJ format")
    void shouldSaveFarmaciaWithValidCnpjFormat() {
        // Given
        Farmacia farmaciaComCnpjNumerico = new Farmacia();
        farmaciaComCnpjNumerico.setRazaoSocial("Farmácia Teste");
        farmaciaComCnpjNumerico.setCnpj("12345678000190");

        when(farmaciaRepository.findByCnpj("12345678000190")).thenReturn(Optional.empty());
        when(farmaciaRepository.save(farmaciaComCnpjNumerico)).thenReturn(farmaciaComCnpjNumerico);

        // When/Then
        assertThatCode(() -> farmaciaService.salvar(farmaciaComCnpjNumerico))
            .doesNotThrowAnyException();

        verify(farmaciaRepository).save(farmaciaComCnpjNumerico);
    }

    @Test
    @DisplayName("Should save farmacia with valid email format")
    void shouldSaveFarmaciaWithValidEmailFormat() {
        // Given
        Farmacia farmaciaComEmail = new Farmacia();
        farmaciaComEmail.setRazaoSocial("Farmácia Teste");
        farmaciaComEmail.setCnpj("12345678000190");
        farmaciaComEmail.setEmailContato("teste@farmacia.com.br");

        when(farmaciaRepository.findByCnpj("12345678000190")).thenReturn(Optional.empty());
        when(farmaciaRepository.save(farmaciaComEmail)).thenReturn(farmaciaComEmail);

        // When/Then
        assertThatCode(() -> farmaciaService.salvar(farmaciaComEmail))
            .doesNotThrowAnyException();

        verify(farmaciaRepository).save(farmaciaComEmail);
    }

    @Test
    @DisplayName("Should allow saving farmacia with null email")
    void shouldAllowSavingFarmaciaWithNullEmail() {
        // Given
        Farmacia farmaciaSemEmail = new Farmacia();
        farmaciaSemEmail.setRazaoSocial("Farmácia Teste");
        farmaciaSemEmail.setCnpj("12345678000190");
        farmaciaSemEmail.setEmailContato(null);

        when(farmaciaRepository.findByCnpj("12345678000190")).thenReturn(Optional.empty());
        when(farmaciaRepository.save(farmaciaSemEmail)).thenReturn(farmaciaSemEmail);

        // When/Then
        assertThatCode(() -> farmaciaService.salvar(farmaciaSemEmail))
            .doesNotThrowAnyException();

        verify(farmaciaRepository).save(farmaciaSemEmail);
    }
}