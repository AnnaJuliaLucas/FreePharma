package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador;
import com.annaehugo.freepharma.domain.entity.administrativo.Perfil;
import com.annaehugo.freepharma.domain.entity.administrativo.Unidade;
import com.annaehugo.freepharma.domain.repository.administrativo.UsuarioAdministradorRepository;
import com.annaehugo.freepharma.domain.repository.administrativo.PerfilRepository;
import com.annaehugo.freepharma.domain.repository.administrativo.UnidadeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioAdministradorService Tests")
class UsuarioAdministradorServiceTest {

    @Mock
    private UsuarioAdministradorRepository usuarioRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private UnidadeRepository unidadeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioAdministradorService usuarioService;

    private UsuarioAdministrador usuario;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioAdministradorService(
            usuarioRepository, perfilRepository, unidadeRepository, passwordEncoder
        );

        usuario = new UsuarioAdministrador();
        usuario.setId(1L);
        usuario.setLogin("testuser");
        usuario.setSenha("plainPassword");
        usuario.setNome("Test User");
        usuario.setEmail("test@example.com");
        usuario.setDataCadastro(new Date());
        usuario.setStatus("ATIVO");
        usuario.setAtivo(true);
    }

    @Test
    @DisplayName("Should list all users successfully")
    void shouldListAllUsersSuccessfully() {
        // Given
        List<UsuarioAdministrador> usuarios = Arrays.asList(usuario, new UsuarioAdministrador());
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // When
        List<UsuarioAdministrador> result = usuarioService.listarTodos();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(usuario);
        verify(usuarioRepository).findAll();
    }

    @Test
    @DisplayName("Should list users by status successfully")
    void shouldListUsersByStatusSuccessfully() {
        // Given
        String status = "ATIVO";
        List<UsuarioAdministrador> usuarios = Arrays.asList(usuario);
        when(usuarioRepository.findByStatus(status)).thenReturn(usuarios);

        // When
        List<UsuarioAdministrador> result = usuarioService.listarPorStatus(status);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(usuario);
        verify(usuarioRepository).findByStatus(status);
    }

    @Test
    @DisplayName("Should list users by unit successfully")
    void shouldListUsersByUnitSuccessfully() {
        // Given
        Long unidadeId = 1L;
        List<UsuarioAdministrador> usuarios = Arrays.asList(usuario);
        when(usuarioRepository.findByUnidadesAcessoId(unidadeId)).thenReturn(usuarios);

        // When
        List<UsuarioAdministrador> result = usuarioService.listarPorUnidade(unidadeId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(usuario);
        verify(usuarioRepository).findByUnidadesAcessoId(unidadeId);
    }

    @Test
    @DisplayName("Should find user by id successfully")
    void shouldFindUserByIdSuccessfully() {
        // Given
        Long id = 1L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        // When
        Optional<UsuarioAdministrador> result = usuarioService.buscarPorId(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(usuario);
        verify(usuarioRepository).findById(id);
    }

    @Test
    @DisplayName("Should find user by login successfully")
    void shouldFindUserByLoginSuccessfully() {
        // Given
        String login = "testuser";
        when(usuarioRepository.findByLogin(login)).thenReturn(Optional.of(usuario));

        // When
        Optional<UsuarioAdministrador> result = usuarioService.buscarPorLogin(login);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(usuario);
        verify(usuarioRepository).findByLogin(login);
    }

    @Test
    @DisplayName("Should find user by email successfully")
    void shouldFindUserByEmailSuccessfully() {
        // Given
        String email = "test@example.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // When
        Optional<UsuarioAdministrador> result = usuarioService.buscarPorEmail(email);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(usuario);
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Should save new user successfully")
    void shouldSaveNewUserSuccessfully() {
        // Given
        UsuarioAdministrador novoUsuario = new UsuarioAdministrador();
        novoUsuario.setLogin("newuser");
        novoUsuario.setSenha("password123");
        novoUsuario.setNome("New User");
        novoUsuario.setEmail("new@example.com");

        when(usuarioRepository.findByLogin("newuser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(UsuarioAdministrador.class))).thenReturn(novoUsuario);

        // When
        UsuarioAdministrador result = usuarioService.salvar(novoUsuario);

        // Then
        assertThat(result).isNotNull();
        assertThat(novoUsuario.getDataCadastro()).isNotNull();
        assertThat(novoUsuario.getStatus()).isEqualTo("ATIVO");
        verify(usuarioRepository).findByLogin("newuser");
        verify(usuarioRepository).findByEmail("new@example.com");
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(novoUsuario);
    }

    @Test
    @DisplayName("Should throw exception when saving user with duplicate login")
    void shouldThrowExceptionWhenSavingUserWithDuplicateLogin() {
        // Given
        UsuarioAdministrador novoUsuario = new UsuarioAdministrador();
        novoUsuario.setLogin("testuser");
        novoUsuario.setSenha("password123");
        novoUsuario.setNome("New User");

        when(usuarioRepository.findByLogin("testuser")).thenReturn(Optional.of(usuario));

        // When/Then
        assertThatThrownBy(() -> usuarioService.salvar(novoUsuario))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Login já existe");

        verify(usuarioRepository).findByLogin("testuser");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when saving user with duplicate email")
    void shouldThrowExceptionWhenSavingUserWithDuplicateEmail() {
        // Given
        UsuarioAdministrador novoUsuario = new UsuarioAdministrador();
        novoUsuario.setLogin("newuser");
        novoUsuario.setSenha("password123");
        novoUsuario.setNome("New User");
        novoUsuario.setEmail("test@example.com");

        when(usuarioRepository.findByLogin("newuser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        // When/Then
        assertThatThrownBy(() -> usuarioService.salvar(novoUsuario))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Email já cadastrado");

        verify(usuarioRepository).findByLogin("newuser");
        verify(usuarioRepository).findByEmail("test@example.com");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when saving user without login")
    void shouldThrowExceptionWhenSavingUserWithoutLogin() {
        // Given
        UsuarioAdministrador usuarioInvalido = new UsuarioAdministrador();
        usuarioInvalido.setLogin("");
        usuarioInvalido.setSenha("password123");
        usuarioInvalido.setNome("User");

        // When/Then
        assertThatThrownBy(() -> usuarioService.salvar(usuarioInvalido))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Login é obrigatório");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when saving user with short login")
    void shouldThrowExceptionWhenSavingUserWithShortLogin() {
        // Given
        UsuarioAdministrador usuarioInvalido = new UsuarioAdministrador();
        usuarioInvalido.setLogin("ab");
        usuarioInvalido.setSenha("password123");
        usuarioInvalido.setNome("User");

        // When/Then
        assertThatThrownBy(() -> usuarioService.salvar(usuarioInvalido))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Login deve ter pelo menos 3 caracteres");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when saving user without name")
    void shouldThrowExceptionWhenSavingUserWithoutName() {
        // Given
        UsuarioAdministrador usuarioInvalido = new UsuarioAdministrador();
        usuarioInvalido.setLogin("validlogin");
        usuarioInvalido.setSenha("password123");
        usuarioInvalido.setNome("");

        // When/Then
        assertThatThrownBy(() -> usuarioService.salvar(usuarioInvalido))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Nome é obrigatório");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when saving user with invalid email")
    void shouldThrowExceptionWhenSavingUserWithInvalidEmail() {
        // Given
        UsuarioAdministrador usuarioInvalido = new UsuarioAdministrador();
        usuarioInvalido.setLogin("validlogin");
        usuarioInvalido.setSenha("password123");
        usuarioInvalido.setNome("User");
        usuarioInvalido.setEmail("invalid-email");

        // When/Then
        assertThatThrownBy(() -> usuarioService.salvar(usuarioInvalido))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Email inválido");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        Long id = 1L;
        UsuarioAdministrador usuarioAtualizado = new UsuarioAdministrador();
        usuarioAtualizado.setLogin("newlogin");
        usuarioAtualizado.setNome("Updated User");
        usuarioAtualizado.setEmail("updated@example.com");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(UsuarioAdministrador.class))).thenReturn(usuarioAtualizado);

        // When
        UsuarioAdministrador result = usuarioService.atualizar(id, usuarioAtualizado);

        // Then
        assertThat(result).isNotNull();
        assertThat(usuarioAtualizado.getId()).isEqualTo(id);
        assertThat(usuarioAtualizado.getLogin()).isEqualTo(usuario.getLogin()); // Login não deve mudar
        assertThat(usuarioAtualizado.getDataCadastro()).isEqualTo(usuario.getDataCadastro()); // Data não deve mudar
        
        verify(usuarioRepository).findById(id);
        verify(usuarioRepository).save(usuarioAtualizado);
    }

    @Test
    @DisplayName("Should update user password when provided")
    void shouldUpdateUserPasswordWhenProvided() {
        // Given
        Long id = 1L;
        UsuarioAdministrador usuarioAtualizado = new UsuarioAdministrador();
        usuarioAtualizado.setLogin("newlogin");
        usuarioAtualizado.setNome("Updated User");
        usuarioAtualizado.setSenha("newpassword");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("newpassword")).thenReturn("hashedNewPassword");
        when(usuarioRepository.save(any(UsuarioAdministrador.class))).thenReturn(usuarioAtualizado);

        // When
        usuarioService.atualizar(id, usuarioAtualizado);

        // Then
        verify(passwordEncoder).encode("newpassword");
        verify(usuarioRepository).save(usuarioAtualizado);
    }

    @Test
    @DisplayName("Should keep existing password when not provided")
    void shouldKeepExistingPasswordWhenNotProvided() {
        // Given
        Long id = 1L;
        UsuarioAdministrador usuarioAtualizado = new UsuarioAdministrador();
        usuarioAtualizado.setLogin("newlogin");
        usuarioAtualizado.setNome("Updated User");
        usuarioAtualizado.setSenha(null);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(UsuarioAdministrador.class))).thenReturn(usuarioAtualizado);

        // When
        usuarioService.atualizar(id, usuarioAtualizado);

        // Then
        assertThat(usuarioAtualizado.getSenha()).isEqualTo(usuario.getSenha());
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository).save(usuarioAtualizado);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // Given
        Long id = 999L;
        UsuarioAdministrador usuarioAtualizado = new UsuarioAdministrador();

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> usuarioService.atualizar(id, usuarioAtualizado))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Usuário não encontrado");

        verify(usuarioRepository).findById(id);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        Long id = 1L;
        when(usuarioRepository.existsById(id)).thenReturn(true);

        // When
        usuarioService.deletar(id);

        // Then
        verify(usuarioRepository).existsById(id);
        verify(usuarioRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        // Given
        Long id = 999L;
        when(usuarioRepository.existsById(id)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> usuarioService.deletar(id))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Usuário não encontrado");

        verify(usuarioRepository).existsById(id);
        verify(usuarioRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should activate user successfully")
    void shouldActivateUserSuccessfully() {
        // Given
        Long id = 1L;
        usuario.setStatus("INATIVO");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // When
        usuarioService.ativar(id);

        // Then
        assertThat(usuario.getStatus()).isEqualTo("ATIVO");
        verify(usuarioRepository).findById(id);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Should deactivate user successfully")
    void shouldDeactivateUserSuccessfully() {
        // Given
        Long id = 1L;
        usuario.setStatus("ATIVO");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // When
        usuarioService.inativar(id);

        // Then
        assertThat(usuario.getStatus()).isEqualTo("INATIVO");
        verify(usuarioRepository).findById(id);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Should block user successfully")
    void shouldBlockUserSuccessfully() {
        // Given
        Long id = 1L;
        usuario.setStatus("ATIVO");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // When
        usuarioService.bloquear(id);

        // Then
        assertThat(usuario.getStatus()).isEqualTo("BLOQUEADO");
        verify(usuarioRepository).findById(id);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Should change password successfully")
    void shouldChangePasswordSuccessfully() {
        // Given
        Long id = 1L;
        String senhaAtual = "currentPassword";
        String novaSenha = "newPassword123";
        
        usuario.setSenha("hashedCurrentPassword");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senhaAtual, "hashedCurrentPassword")).thenReturn(true);
        when(passwordEncoder.encode(novaSenha)).thenReturn("hashedNewPassword");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // When
        usuarioService.alterarSenha(id, senhaAtual, novaSenha);

        // Then
        verify(passwordEncoder).matches(senhaAtual, "hashedCurrentPassword");
        verify(passwordEncoder).encode(novaSenha);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Should throw exception when changing password with incorrect current password")
    void shouldThrowExceptionWhenChangingPasswordWithIncorrectCurrentPassword() {
        // Given
        Long id = 1L;
        String senhaAtual = "wrongPassword";
        String novaSenha = "newPassword123";
        
        usuario.setSenha("hashedCurrentPassword");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senhaAtual, "hashedCurrentPassword")).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> usuarioService.alterarSenha(id, senhaAtual, novaSenha))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Senha atual incorreta");

        verify(passwordEncoder).matches(senhaAtual, "hashedCurrentPassword");
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when changing password with short new password")
    void shouldThrowExceptionWhenChangingPasswordWithShortNewPassword() {
        // Given
        Long id = 1L;
        String senhaAtual = "currentPassword";
        String novaSenha = "123"; // Muito curta
        
        usuario.setSenha("hashedCurrentPassword");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senhaAtual, "hashedCurrentPassword")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> usuarioService.alterarSenha(id, senhaAtual, novaSenha))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Nova senha deve ter pelo menos 6 caracteres");

        verify(passwordEncoder).matches(senhaAtual, "hashedCurrentPassword");
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should assign profiles to user successfully")
    void shouldAssignProfilesToUserSuccessfully() {
        // Given
        Long usuarioId = 1L;
        List<Long> perfilIds = Arrays.asList(1L, 2L);
        List<Perfil> perfis = Arrays.asList(new Perfil(), new Perfil());

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(perfilRepository.findAllById(perfilIds)).thenReturn(perfis);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // When
        usuarioService.atribuirPerfis(usuarioId, perfilIds);

        // Then
        assertThat(usuario.getPerfis()).isEqualTo(perfis);
        verify(usuarioRepository).findById(usuarioId);
        verify(perfilRepository).findAllById(perfilIds);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Should register access successfully")
    void shouldRegisterAccessSuccessfully() {
        // Given
        Long id = 1L;

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // When
        usuarioService.registrarAcesso(id);

        // Then
        assertThat(usuario.getUltimoAcesso()).isNotNull();
        verify(usuarioRepository).findById(id);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Should throw exception when registering access for non-existent user")
    void shouldThrowExceptionWhenRegisteringAccessForNonExistentUser() {
        // Given
        Long id = 999L;

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> usuarioService.registrarAcesso(id))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Usuário não encontrado");

        verify(usuarioRepository).findById(id);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should validate user with non-existent unit access")
    void shouldValidateUserWithNonExistentUnitAccess() {
        // Given
        Unidade unidade = new Unidade();
        unidade.setId(999L);
        
        UsuarioAdministrador usuarioComUnidades = new UsuarioAdministrador();
        usuarioComUnidades.setLogin("testuser");
        usuarioComUnidades.setNome("Test User");
        usuarioComUnidades.setSenha("password");
        usuarioComUnidades.setUnidadesAcesso(Arrays.asList(unidade));

        //when(usuarioRepository.findByLogin("testuser")).thenReturn(Optional.empty());
        when(unidadeRepository.existsById(999L)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> usuarioService.salvar(usuarioComUnidades))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Unidade não encontrada: 999");

        verify(unidadeRepository).existsById(999L);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should save user with valid unit access")
    void shouldSaveUserWithValidUnitAccess() {
        // Given
        Unidade unidade = new Unidade();
        unidade.setId(1L);
        
        UsuarioAdministrador usuarioComUnidades = new UsuarioAdministrador();
        usuarioComUnidades.setLogin("testuser");
        usuarioComUnidades.setNome("Test User");
        usuarioComUnidades.setSenha("password");
        usuarioComUnidades.setUnidadesAcesso(Arrays.asList(unidade));

        when(usuarioRepository.findByLogin("testuser")).thenReturn(Optional.empty());
        when(unidadeRepository.existsById(1L)).thenReturn(true);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(UsuarioAdministrador.class))).thenReturn(usuarioComUnidades);

        // When
        UsuarioAdministrador result = usuarioService.salvar(usuarioComUnidades);

        // Then
        assertThat(result).isNotNull();
        verify(unidadeRepository).existsById(1L);
        verify(usuarioRepository).save(usuarioComUnidades);
    }
}