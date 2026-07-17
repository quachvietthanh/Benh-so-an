package com.benhsoan.infrastructure.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtTokenProvider -- Session Security Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private static final String SECRET = Base64.getEncoder().encodeToString(
            "MySuperSecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong!!".getBytes()
    );

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(SECRET, 3600000);
    }

    @Test
    @DisplayName("Generate token for username without roles")
    void generateTokenForUsername() {
        String token = tokenProvider.generateToken("admin");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("Generate token with roles")
    void generateTokenWithRoles() {
        String token = tokenProvider.generateToken("doctor1", List.of("DOCTOR", "USER"));
        assertNotNull(token);
        assertEquals("doctor1", tokenProvider.getUsernameFromToken(token));
        assertTrue(tokenProvider.getRolesFromToken(token).contains("DOCTOR"));
    }

    @Test
    @DisplayName("Validate valid token returns true")
    void validateValidToken() {
        String token = tokenProvider.generateToken("admin");
        assertTrue(tokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Validate expired token returns false")
    void validateExpiredToken() throws Exception {
        JwtTokenProvider shortLived = new JwtTokenProvider(SECRET, 1);
        String token = shortLived.generateToken("admin");
        Thread.sleep(10);
        assertFalse(shortLived.validateToken(token));
    }

    @Test
    @DisplayName("Validate malformed token returns false")
    void validateMalformedToken() {
        assertFalse(tokenProvider.validateToken("invalid-token-here"));
    }

    @Test
    @DisplayName("Validate empty token returns false")
    void validateEmptyToken() {
        assertFalse(tokenProvider.validateToken(""));
    }

    @Test
    @DisplayName("Username extraction matches original")
    void getUsernameFromToken() {
        String token = tokenProvider.generateToken("doctor1");
        assertEquals("doctor1", tokenProvider.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Roles extraction matches original")
    void getRolesFromToken() {
        String token = tokenProvider.generateToken("receptionist", List.of("RECEPTIONIST"));
        List<String> roles = tokenProvider.getRolesFromToken(token);
        assertEquals(1, roles.size());
        assertTrue(roles.contains("RECEPTIONIST"));
    }

    @Test
    @DisplayName("Token blacklisted after invalidation")
    void tokenBlacklistedAfterInvalidate() {
        String token = tokenProvider.generateToken("admin");
        assertTrue(tokenProvider.validateToken(token));
        tokenProvider.invalidateToken(token);
        assertFalse(tokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Invalidate non-existent token does not throw")
    void invalidateInvalidTokenDoesNotThrow() {
        assertDoesNotThrow(() -> tokenProvider.invalidateToken("some-invalid-token"));
    }

    @Test
    @DisplayName("Expiration date is not null for valid token")
    void getExpirationDateFromToken() {
        String token = tokenProvider.generateToken("admin");
        assertNotNull(tokenProvider.getExpirationDateFromToken(token));
    }

    @Test
    @DisplayName("Token not expiring soon returns false")
    void isTokenNotExpiringSoon() {
        JwtTokenProvider longLived = new JwtTokenProvider(SECRET, 600000);
        String token = longLived.generateToken("admin");
        assertFalse(longLived.isTokenExpiringSoon(token));
    }

    @Test
    @DisplayName("Generate token and validate full lifecycle")
    void tokenFullLifecycle() {
        String token = tokenProvider.generateToken("nurse", List.of("NURSE"));
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("nurse", tokenProvider.getUsernameFromToken(token));
        assertTrue(tokenProvider.getRolesFromToken(token).contains("NURSE"));

        tokenProvider.invalidateToken(token);
        assertFalse(tokenProvider.validateToken(token));
    }
}
