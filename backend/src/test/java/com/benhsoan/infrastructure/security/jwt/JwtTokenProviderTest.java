package com.benhsoan.infrastructure.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtTokenProvider - Session Security Tests")
class JwtTokenProviderTest {

    private static final String SECRET = "ThisIsAVeryLongSecretKeyForJwtTokenSigningThatIsAtLeast256BitsLong!!";
    private static final long EXPIRATION_MS = 3600000; // 1 hour
    private static final long SHORT_EXPIRATION_MS = 3000; // 3s for testing

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(SECRET, EXPIRATION_MS);
    }

    @Test
    @DisplayName("Should generate valid token with username and roles")
    void generateValidToken() {
        String token = provider.generateToken("doctor1", List.of("DOCTOR"));

        assertNotNull(token);
        assertTrue(provider.validateToken(token));
        assertEquals("doctor1", provider.getUsernameFromToken(token));
        assertEquals(List.of("DOCTOR"), provider.getRolesFromToken(token));
    }

    @Test
    @DisplayName("Should generate token with empty roles list")
    void generateTokenWithEmptyRoles() {
        String token = provider.generateToken("nurse1");

        assertNotNull(token);
        assertTrue(provider.validateToken(token));
        assertEquals("nurse1", provider.getUsernameFromToken(token));
        assertTrue(provider.getRolesFromToken(token).isEmpty());
    }

    @Test
    @DisplayName("Should validate token correctly")
    void validateToken() {
        String token = provider.generateToken("admin", List.of("ADMIN"));
        assertTrue(provider.validateToken(token));
    }

    @Test
    @DisplayName("Should reject invalid token")
    void rejectInvalidToken() {
        assertFalse(provider.validateToken("invalid-token-here"));
        assertFalse(provider.validateToken(""));
        assertFalse(provider.validateToken("Bearer fake-token"));
    }

    @Test
    @DisplayName("Should reject expired token")
    void rejectExpiredToken() throws InterruptedException {
        JwtTokenProvider shortProvider = new JwtTokenProvider(SECRET, SHORT_EXPIRATION_MS);
        String token = shortProvider.generateToken("user1", List.of("USER"));

        assertTrue(shortProvider.validateToken(token));

        // Wait for token to expire
        Thread.sleep(SHORT_EXPIRATION_MS + 50);

        assertFalse(shortProvider.validateToken(token));
    }

    @Test
    @DisplayName("Should blacklist and invalidate token")
    void invalidateToken() {
        String token = provider.generateToken("user1", List.of("USER"));

        assertTrue(provider.validateToken(token));
        assertFalse(provider.isTokenBlacklisted(token));

        provider.invalidateToken(token);

        assertTrue(provider.isTokenBlacklisted(token));
        assertFalse(provider.validateToken(token));
    }

    @Test
    @DisplayName("Should get correct expiration date")
    void getExpirationDate() {
        String token = provider.generateToken("user1", List.of("USER"));
        Date expiration = provider.getExpirationDateFromToken(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    @DisplayName("Should detect token expiring soon")
    void tokenExpiringSoon() {
        JwtTokenProvider shortProvider = new JwtTokenProvider(SECRET, SHORT_EXPIRATION_MS);
        String token = shortProvider.generateToken("user1", List.of("USER"));

        // With short expiration, should be expiring soon
        assertTrue(shortProvider.isTokenExpiringSoon(token));

        // Normal token should not be expiring soon
        String normalToken = provider.generateToken("user2", List.of("USER"));
        assertFalse(provider.isTokenExpiringSoon(normalToken));
    }

    @Test
    @DisplayName("Should handle multiple roles in token")
    void multipleRoles() {
        String token = provider.generateToken("multi_role_user",
                List.of("ADMIN", "DOCTOR", "PHARMACIST"));

        List<String> roles = provider.getRolesFromToken(token);
        assertTrue(roles.contains("ADMIN"));
        assertTrue(roles.contains("DOCTOR"));
        assertTrue(roles.contains("PHARMACIST"));
        assertEquals(3, roles.size());
    }

    @Test
    @DisplayName("Different users should have different tokens")
    void differentUsersDifferentTokens() {
        String token1 = provider.generateToken("user1", List.of("DOCTOR"));
        String token2 = provider.generateToken("user2", List.of("NURSE"));

        assertNotEquals(token1, token2);
        assertEquals("user1", provider.getUsernameFromToken(token1));
        assertEquals("user2", provider.getUsernameFromToken(token2));
    }

    @Test
    @DisplayName("Blacklisted tokens should be rejected even if valid signature")
    void blacklistedTokensRejected() {
        String token = provider.generateToken("user1", List.of("USER"));

        assertTrue(provider.validateToken(token));

        provider.invalidateToken(token);
        assertFalse(provider.validateToken(token)); // Blacklisted
    }

    @Test
    @DisplayName("Should handle invalid token gracefully")
    void handleInvalidTokenGracefully() {
        assertFalse(provider.validateToken(null));
        assertFalse(provider.validateToken(""));
        assertFalse(provider.validateToken("not.a.jwt.token"));

        // Should not throw exception
        assertNull(provider.getExpirationDateFromToken("invalid"));
        // Invalid token is treated as expiring soon for security
        assertTrue(provider.isTokenExpiringSoon("invalid"));
    }
}
