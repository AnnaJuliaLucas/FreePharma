package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.application.dto.administrativo.UsuarioAdministradorDTO;
import com.annaehugo.freepharma.application.mapper.UsuarioAdministradorMapper;
import com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador;
import com.annaehugo.freepharma.domain.repository.administrativo.UsuarioAdministradorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService Tests")
class AuthenticationServiceTest {

    @Mock
    private UsuarioAdministradorRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioAdministradorMapper usuarioAdministradorMapper;

    private AuthenticationService authenticationService;

    private UsuarioAdministrador usuario;
    private UsuarioAdministradorDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(
            usuarioRepository, passwordEncoder, jwtService, usuarioAdministradorMapper , authenticationManager
        );

        usuario = new UsuarioAdministrador();
        usuario.setId(1L);
        usuario.setEmail("test@example.com");
        usuario.setSenha("hashedPassword");
        usuario.setNome("Test User");
        usuario.setLogin("testuser");
        usuario.setDataCadastro(new Date());
        usuario.setStatus("ATIVO");
        usuario.setAtivo(true);

        usuarioDTO = new UsuarioAdministradorDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setNome("Test User");
        usuarioDTO.setLogin("testuser");
        usuarioDTO.setStatus("ATIVO");
        usuarioDTO.setAtivo(true);

        when(usuarioAdministradorMapper.toDto(any(UsuarioAdministrador.class))).thenReturn(usuarioDTO);
    }

    @Test
    @DisplayName("Should load user by username successfully")
    void shouldLoadUserByUsernameSuccessfully() {
        // Given
        String email = "test@example.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // When
        UserDetails userDetails = authenticationService.loadUserByUsername(email);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo("hashedPassword");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void shouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        // Given
        String email = "notfound@example.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> authenticationService.loadUserByUsername(email))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining("Usuário não encontrado: " + email);
        
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Should load user with locked account when user is inactive")
    void shouldLoadUserWithLockedAccountWhenUserIsInactive() {
        // Given
        String email = "test@example.com";
        usuario.setAtivo(false);
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // When
        UserDetails userDetails = authenticationService.loadUserByUsername(email);

        // Then
        assertThat(userDetails.isAccountNonLocked()).isFalse();
        assertThat(userDetails.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should authenticate successfully with valid credentials")
    void shouldAuthenticateSuccessfullyWithValidCredentials() {
        // Given
        var request = new AuthenticationService.AuthenticationRequest("test@example.com", "password");
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn(refreshToken);

        // When
        var response = authenticationService.authenticate(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(response.getUsuario()).isEqualTo(usuarioDTO);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository, times(2)).findByEmail(request.getEmail());
        verify(jwtService).generateToken(any(UserDetails.class));
        verify(jwtService).generateRefreshToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Should throw exception when authentication fails")
    void shouldThrowExceptionWhenAuthenticationFails() {
        // Given
        var request = new AuthenticationService.AuthenticationRequest("test@example.com", "wrongpassword");
        
        doThrow(new BadCredentialsException("Invalid credentials"))
            .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // When/Then
        assertThatThrownBy(() -> authenticationService.authenticate(request))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessage("Invalid credentials");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should throw exception when user not found after authentication")
    void shouldThrowExceptionWhenUserNotFoundAfterAuthentication() {
        // Given
        var request = new AuthenticationService.AuthenticationRequest("test@example.com", "password");
        
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> authenticationService.authenticate(request))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Usuário não encontrado após autenticação");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository).findByEmail(request.getEmail());
    }

    @Test
    @DisplayName("Should throw exception when user account is inactive")
    void shouldThrowExceptionWhenUserAccountIsInactive() {
        // Given
        var request = new AuthenticationService.AuthenticationRequest("test@example.com", "password");
        usuario.setAtivo(false);
        
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuario));

        // When/Then
        assertThatThrownBy(() -> authenticationService.authenticate(request))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Conta do usuário está inativa");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository).findByEmail(request.getEmail());
    }

    @Test
    @DisplayName("Should refresh token successfully with valid refresh token")
    void shouldRefreshTokenSuccessfullyWithValidRefreshToken() {
        // Given
        String refreshToken = "valid-refresh-token";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        when(jwtService.extractUsername(refreshToken)).thenReturn(usuario.getEmail());
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));
        when(jwtService.isTokenValid(eq(refreshToken), any(UserDetails.class))).thenReturn(true);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(newAccessToken);
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn(newRefreshToken);

        // When
        var response = authenticationService.refreshToken(refreshToken);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);
        assertThat(response.getUsuario()).isEqualTo(usuarioDTO);

        verify(jwtService).extractUsername(refreshToken);
        verify(jwtService).isTokenValid(eq(refreshToken), any(UserDetails.class));
        verify(jwtService).generateToken(any(UserDetails.class));
        verify(jwtService).generateRefreshToken(any(UserDetails.class));
        verify(usuarioRepository, times(2)).findByEmail(usuario.getEmail());
    }

    @Test
    @DisplayName("Should throw exception when refresh token is invalid")
    void shouldThrowExceptionWhenRefreshTokenIsInvalid() {
        // Given
        String refreshToken = "invalid-refresh-token";

        when(jwtService.extractUsername(refreshToken)).thenReturn(usuario.getEmail());
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));
        when(jwtService.isTokenValid(eq(refreshToken), any(UserDetails.class))).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> authenticationService.refreshToken(refreshToken))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Refresh token inválido ou expirado");

        verify(jwtService).extractUsername(refreshToken);
        verify(jwtService).isTokenValid(eq(refreshToken), any(UserDetails.class));
        verify(jwtService, never()).generateToken(any());
        verify(jwtService, never()).generateRefreshToken(any());
    }

    @Test
    @DisplayName("Should throw exception when user not found during token refresh")
    void shouldThrowExceptionWhenUserNotFoundDuringTokenRefresh() {
        // Given
        String refreshToken = "valid-refresh-token";

        when(jwtService.extractUsername(refreshToken)).thenReturn(usuario.getEmail());
        when(usuarioRepository.findByEmail(usuario.getEmail()))
            .thenReturn(Optional.of(usuario))  // First call for loadUserByUsername
            .thenReturn(Optional.empty());     // Second call for getting user entity

        when(jwtService.isTokenValid(eq(refreshToken), any(UserDetails.class))).thenReturn(true);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("new-refresh-token");

        // When/Then
        assertThatThrownBy(() -> authenticationService.refreshToken(refreshToken))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Usuário não encontrado ao renovar token");

        verify(usuarioRepository, times(2)).findByEmail(usuario.getEmail());
    }

    @Test
    @DisplayName("Should handle authentication request with null email")
    void shouldHandleAuthenticationRequestWithNullEmail() {
        // Given
        var request = new AuthenticationService.AuthenticationRequest();
        request.setEmail(null);
        request.setSenha("password");

        // When/Then
        assertThatThrownBy(() -> authenticationService.authenticate(request))
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should handle authentication request with null password")
    void shouldHandleAuthenticationRequestWithNullPassword() {
        // Given
        var request = new AuthenticationService.AuthenticationRequest();
        request.setEmail("test@example.com");
        request.setSenha(null);

        // When/Then
        assertThatThrownBy(() -> authenticationService.authenticate(request))
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should create AuthenticationRequest correctly")
    void shouldCreateAuthenticationRequestCorrectly() {
        // Given
        String email = "test@example.com";
        String senha = "password";

        // When
        var request1 = new AuthenticationService.AuthenticationRequest();
        request1.setEmail(email);
        request1.setSenha(senha);

        var request2 = new AuthenticationService.AuthenticationRequest(email, senha);

        // Then
        assertThat(request1.getEmail()).isEqualTo(email);
        assertThat(request1.getSenha()).isEqualTo(senha);
        assertThat(request2.getEmail()).isEqualTo(email);
        assertThat(request2.getSenha()).isEqualTo(senha);
    }

    @Test
    @DisplayName("Should build AuthenticationResponse correctly")
    void shouldBuildAuthenticationResponseCorrectly() {
        // Given
        String accessToken = "access-token";
        String refreshToken = "refresh-token";


        usuarioDTO = usuarioAdministradorMapper.toDto(usuario);
        // When
        var response = AuthenticationService.AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .usuario(usuarioDTO)
            .build();

        // Then
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(response.getUsuario()).isEqualTo(usuarioDTO);
    }
}