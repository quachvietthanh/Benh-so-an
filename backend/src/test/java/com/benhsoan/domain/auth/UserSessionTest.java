package com.benhsoan.domain.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserSession - Session Security Tests")
class UserSessionTest {

    @Test
    @DisplayName("Active session should not be marked expired")
    void activeSessionNotExpired() {
        UserSession session = UserSession.create(
                UUID.randomUUID(),
                "token-hash",
                Instant.now().plusSeconds(3600)
        );

        assertFalse(session.isExpired(Instant.now()));
        assertTrue(session.isActive(Instant.now(), Duration.ofMinutes(30)));
    }

    @Test
    @DisplayName("Expired session should be detected")
    void expiredSession() {
        UserSession session = UserSession.create(
                UUID.randomUUID(),
                "token-hash",
                Instant.now().minusSeconds(60)
        );

        assertTrue(session.isExpired(Instant.now()));
    }

    @Test
    @DisplayName("Revoked session should not be active")
    void revokedSession() {
        UserSession session = UserSession.create(
                UUID.randomUUID(),
                "token-hash",
                Instant.now().plusSeconds(3600)
        );

        assertTrue(session.isActive(Instant.now(), Duration.ofMinutes(30)));
        session.revoke(Instant.now());
        assertTrue(session.isRevoked());
        assertFalse(session.isActive(Instant.now(), Duration.ofMinutes(30)));
    }

    @Test
    @DisplayName("Refresh should update last used and expiry")
    void refreshSession() {
        UserSession session = UserSession.create(
                UUID.randomUUID(),
                "old-token-hash",
                Instant.now().minusSeconds(60)
        );

        Duration timeout = Duration.ofHours(1);
        session.refresh(timeout);

        assertFalse(session.isExpired(Instant.now()));
        assertTrue(session.isActive(Instant.now(), Duration.ofMinutes(30)));
    }

    @Test
    @DisplayName("Idle timeout should expire inactive session")
    void idleTimeoutExpires() {
        UserSession session = UserSession.create(
                UUID.randomUUID(),
                "token-hash",
                Instant.now().plusSeconds(3600)
        );

        Duration idleTimeout = Duration.ofMinutes(15);
        assertTrue(session.isActive(Instant.now(), idleTimeout));

        // Simulate idle timeout by updating lastUsed to far in the past
        session.updateLastUsed(Instant.now().minusSeconds(1800));

        assertTrue(session.isIdleTimeout(Instant.now(), idleTimeout));
        assertFalse(session.isActive(Instant.now(), idleTimeout));
    }

    @Test
    @DisplayName("Session restore should recreate from persistence")
    void sessionRestore() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String tokenHash = "restored-hash";
        Instant expiresAt = Instant.now().plusSeconds(3600);
        Instant createdAt = Instant.now().minusSeconds(86400);
        Instant lastUsedAt = Instant.now().minusSeconds(3600);
        Instant revokedAt = null;

        UserSession session = UserSession.restore(
                id, userId, tokenHash, expiresAt, createdAt, lastUsedAt, revokedAt
        );

        assertEquals(id, session.getId());
        assertEquals(userId, session.getUserId());
        assertEquals(tokenHash, session.getTokenHash());
        assertEquals(expiresAt, session.getExpiresAt());
        assertEquals(createdAt, session.getCreatedAt());
        assertEquals(lastUsedAt, session.getLastUsedAt());
        assertFalse(session.isRevoked());
    }
}
