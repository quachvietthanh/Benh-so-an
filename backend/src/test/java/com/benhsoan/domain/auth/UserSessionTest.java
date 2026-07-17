package com.benhsoan.domain.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserSession — Session Lifecycle Tests")
class UserSessionTest {

    private final UUID userId = UUID.randomUUID();
    private final String token = "jwt-token-abc-123";
    private final String refreshToken = "refresh-token-xyz-789";
    private final Instant now = Instant.now();

    private UserSession createActiveSession() {
        return UserSession.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .token(token)
                .refreshToken(refreshToken)
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .loginAt(now)
                .lastActiveAt(now)
                .expiresAt(now.plus(Duration.ofHours(1)))
                .active(true)
                .build();
    }

    @Test
    @DisplayName("New session is active")
    void newSessionIsActive() {
        UserSession session = createActiveSession();
        assertTrue(session.isActive());
        assertFalse(session.isExpired());
    }

    @Test
    @DisplayName("Session can be marked inactive")
    void markInactive() {
        UserSession session = createActiveSession();
        session.markInactive();
        assertFalse(session.isActive());
    }

    @Test
    @DisplayName("Session refresh updates token and timestamps")
    void refreshUpdatesToken() {
        UserSession session = createActiveSession();
        String newToken = "new-jwt-token";
        String newRefreshToken = "new-refresh-token";
        Instant newExpiresAt = now.plus(Duration.ofHours(2));

        session.refresh(newToken, newRefreshToken, newExpiresAt);

        assertEquals(newToken, session.getToken());
        assertEquals(newRefreshToken, session.getRefreshToken());
        assertEquals(newExpiresAt, session.getExpiresAt());
        assertTrue(session.getLastActiveAt().isAfter(now));
    }

    @Test
    @DisplayName("Session after refresh remains active")
    void sessionRemainsActiveAfterRefresh() {
        UserSession session = createActiveSession();
        session.refresh("new-token", "new-refresh", now.plus(Duration.ofHours(2)));
        assertTrue(session.isActive());
    }

    @Test
    @DisplayName("Expired session returns true for isExpired")
    void expiredSession() {
        Instant past = now.minus(Duration.ofHours(2));
        UserSession session = UserSession.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .token(token)
                .expiresAt(past)
                .active(true)
                .build();
        assertTrue(session.isExpired());
        assertTrue(session.isActive());
    }

    @Test
    @DisplayName("Session builder sets all fields correctly")
    void builderSetsAllFields() {
        UUID sessionId = UUID.randomUUID();
        String ip = "10.0.0.1";
        String agent = "Chrome";

        UserSession session = UserSession.builder()
                .id(sessionId)
                .userId(userId)
                .token(token)
                .refreshToken(refreshToken)
                .ipAddress(ip)
                .userAgent(agent)
                .loginAt(now)
                .lastActiveAt(now)
                .expiresAt(now.plus(Duration.ofHours(8)))
                .active(true)
                .build();

        assertEquals(sessionId, session.getId());
        assertEquals(userId, session.getUserId());
        assertEquals(token, session.getToken());
        assertEquals(refreshToken, session.getRefreshToken());
        assertEquals(ip, session.getIpAddress());
        assertEquals(agent, session.getUserAgent());
        assertEquals(now, session.getLoginAt());
        assertEquals(now, session.getLastActiveAt());
        assertTrue(session.isActive());
    }

    @Test
    @DisplayName("Two sessions with same ID are equal")
    void sessionsWithSameIdAreEqual() {
        UUID id = UUID.randomUUID();
        UserSession s1 = UserSession.builder().id(id).userId(userId).build();
        UserSession s2 = UserSession.builder().id(id).userId(UUID.randomUUID()).build();
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }
}
