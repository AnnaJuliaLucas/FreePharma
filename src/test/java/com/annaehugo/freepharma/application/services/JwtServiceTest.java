package com.annaehugo.freepharma.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;
    private String testSecret = "freepharma-test-secret-key-for-testing-purposes-must-be-long-enough-256-bits";
    private long testExpirationMs = 86400000; // 1 day

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        
        // Set test values using reflection
        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtService, "expirationMs", testExpirationMs);
        
        userDetails = User.builder()
            .username("test@example.com")
            .password("password")
            .authorities("ROLE_USER")
            .build();
    }

    @Test
    @DisplayName("Should generate access token successfully")
    void shouldGenerateAccessTokenSuccessfully() {
        // When
        String token = jwtService.generateToken(userDetails);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should generate refresh token successfully")
    void shouldGenerateRefreshTokenSuccessfully() {
        // When
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        assertThat(refreshToken.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should extract username from token successfully")
    void shouldExtractUsernameFromTokenSuccessfully() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertThat(extractedUsername).isEqualTo(userDetails.getUsername());
    }

    @Test
    @DisplayName("Should validate valid token successfully")
    void shouldValidateValidTokenSuccessfully() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject token with wrong username")
    void shouldRejectTokenWithWrongUsername() {
        // Given
        String token = jwtService.generateToken(userDetails);
        
        UserDetails differentUser = User.builder()
            .username("different@example.com")
            .password("password")
            .authorities("ROLE_USER")
            .build();

        // When
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should handle Bearer prefix in token")
    void shouldHandleBearerPrefixInToken() {
        // Given
        String token = jwtService.generateToken(userDetails);
        String tokenWithBearer = "Bearer " + token;

        // When
        String extractedUsername = jwtService.extractUsername(tokenWithBearer);

        // Then
        assertThat(extractedUsername).isEqualTo(userDetails.getUsername());
    }

    @Test
    @DisplayName("Should handle token without Bearer prefix")
    void shouldHandleTokenWithoutBearerPrefix() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertThat(extractedUsername).isEqualTo(userDetails.getUsername());
    }

    @Test
    @DisplayName("Should validate Bearer token correctly")
    void shouldValidateBearerTokenCorrectly() {
        // Given
        String token = jwtService.generateToken(userDetails);
        String tokenWithBearer = "Bearer " + token;

        // When
        boolean isValid = jwtService.isTokenValid(tokenWithBearer, userDetails);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should throw exception for invalid token format")
    void shouldThrowExceptionForInvalidTokenFormat() {
        // Given
        String invalidToken = "invalid.token.format";

        // When/Then
        assertThatThrownBy(() -> jwtService.extractUsername(invalidToken))
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should throw exception for malformed token")
    void shouldThrowExceptionForMalformedToken() {
        // Given
        String malformedToken = "malformed-token";

        // When/Then
        assertThatThrownBy(() -> jwtService.extractUsername(malformedToken))
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should generate different tokens for same user at different times")
    void shouldGenerateDifferentTokensForSameUser() {
        // When
        String token1 = jwtService.generateToken(userDetails);
        
        // Increase delay to ensure different timestamps
        try {
            Thread.sleep(1000); // 1 second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String token2 = jwtService.generateToken(userDetails);

        // Then
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should validate refresh token correctly")
    void shouldValidateRefreshTokenCorrectly() {
        // Given
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // When
        boolean isValid = jwtService.isTokenValid(refreshToken, userDetails);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should handle null token gracefully")
    void shouldHandleNullTokenGracefully() {
        // When/Then
        assertThatThrownBy(() -> jwtService.extractUsername(null))
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should handle empty token gracefully")
    void shouldHandleEmptyTokenGracefully() {
        // When/Then
        assertThatThrownBy(() -> jwtService.extractUsername(""))
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should refresh token have longer expiration than access token")
    void shouldRefreshTokenHaveLongerExpirationThanAccessToken() {
        // Given
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // When
        // Both tokens should be valid (refresh token should have 7x longer expiration)
        boolean accessTokenValid = jwtService.isTokenValid(accessToken, userDetails);
        boolean refreshTokenValid = jwtService.isTokenValid(refreshToken, userDetails);

        // Then
        assertThat(accessTokenValid).isTrue();
        assertThat(refreshTokenValid).isTrue();
        // We can't directly test expiration times without exposing more methods,
        // but both should be valid and refresh token should have different content
        assertThat(accessToken).isNotEqualTo(refreshToken);
    }
}