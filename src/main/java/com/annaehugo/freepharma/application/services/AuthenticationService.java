package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.application.dto.administrativo.UsuarioAdministradorDTO;
import com.annaehugo.freepharma.application.mapper.UsuarioAdministradorMapper;
import com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador;
import com.annaehugo.freepharma.domain.repository.administrativo.UsuarioAdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthenticationService implements UserDetailsService {

    private final UsuarioAdministradorRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioAdministradorMapper usuarioAdministradorMapper;

    @Autowired
    public AuthenticationService(
            UsuarioAdministradorRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UsuarioAdministradorMapper usuarioAdministradorMapper,
            @Lazy AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.usuarioAdministradorMapper = usuarioAdministradorMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioAdministrador usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        List<SimpleGrantedAuthority> authorities = usuario.getPerfis() != null 
            ? usuario.getPerfis().stream()
                .filter(perfil -> perfil.getAtivo())
                .map(perfil -> new SimpleGrantedAuthority("ROLE_" + perfil.getNome().toUpperCase()))
                .collect(Collectors.toList())
            : List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(!usuario.isAtivo())
                .credentialsExpired(false)
                .disabled(!usuario.isAtivo())
                .build();
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        UsuarioAdministrador usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado após autenticação"));

        if (!usuario.isAtivo()) {
            throw new RuntimeException("Conta do usuário está inativa");
        }

        var usuarioDTO = usuarioAdministradorMapper.toDto(usuario);

        UserDetails userDetails = loadUserByUsername(request.getEmail());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .usuario(usuarioDTO)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthenticationResponse refreshToken(String rawRefreshToken) {
        String userEmail = jwtService.extractUsername(rawRefreshToken);
        UserDetails userDetails = loadUserByUsername(userEmail);

        if (!jwtService.isTokenValid(rawRefreshToken, userDetails)) {
            throw new RuntimeException("Refresh token inválido ou expirado");
        }
        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        UsuarioAdministrador usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ao renovar token"));

        var usuarioDTO = usuarioAdministradorMapper.toDto(usuario);

        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .usuario(usuarioDTO)
                .build();
    }

    public static class AuthenticationRequest {
        private String email;
        private String senha;
        public AuthenticationRequest() {}
        public AuthenticationRequest(String email, String senha) {
            this.email = email;
            this.senha = senha;
        }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
    }

    public static class AuthenticationResponse {
        private String accessToken;
        private String refreshToken;
        private UsuarioAdministradorDTO usuario;

        public AuthenticationResponse() {}

        private AuthenticationResponse(String accessToken, String refreshToken, UsuarioAdministradorDTO usuario) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.usuario = usuario;
        }

        public static AuthenticationResponseBuilder builder() { return new AuthenticationResponseBuilder(); }

        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public UsuarioAdministradorDTO getUsuario() { return usuario; }

        public static class AuthenticationResponseBuilder {
            private String accessToken;
            private String refreshToken;
            private UsuarioAdministradorDTO usuario;

            public AuthenticationResponseBuilder accessToken(String accessToken) {
                this.accessToken = accessToken; return this;
            }

            public AuthenticationResponseBuilder refreshToken(String refreshToken) {
                this.refreshToken = refreshToken; return this;
            }

            public AuthenticationResponseBuilder usuario(UsuarioAdministradorDTO usuario) {
                this.usuario = usuario; return this;
            }

            public AuthenticationResponse build() {
                return new AuthenticationResponse(accessToken, refreshToken, usuario);
            }
        }
    }
}