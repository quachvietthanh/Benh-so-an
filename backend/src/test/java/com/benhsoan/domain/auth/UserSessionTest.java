package com.benhsoan.domain.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserSession - Session Security Tests")
class UserSessionTest {

    @Test
    @DisplayName("Active session should not be marked expired")
    void activeSessionNotExpired() {
        UserSession session = UserSession.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .token("valid-token")
                .refreshToken("refresh-token")
                .expiresAt(Instant.now().plusSeconds(3600))
                .active(true)
                .build();

        assertFalse(session.isExpired());
        assertTrue(session.isActive());
    }

    @Test
    @DisplayName("Expired session should be detected")
    void expiredSession() {
        UserSession session = UserSession.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .token("expired-token")
                .expiresAt(Instant.now().minusSeconds(60))
                .active(true)
                .build();

        assertTrue(session.isExpired());
    }

    @Test
    @DisplayName("Mark inactive should deactivate session")
    void markInactive() {
        UserSession session = UserSession.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .token("active-token")
                .expiresAt(Instant.now().plusSeconds(3600))
                .active(true)
                .build();

        assertTrue(session.isActive());
        session.markInactive();
        assertFalse(session.isActive());
    }

    @Test
    @DisplayName("Refresh should update token and expiry")
    void refreshSession() {
        UserSession session = UserSession.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .token("old-token")
                .refreshToken("old-refresh-token")
                .expiresAt(Instant.now().minusSeconds(60))
                .active(true)
                .build();

        Instant newExpiry = Instant.now().plusSeconds(3600);
        session.refresh("new-token", "new-refresh-token", newExpiry);

        assertEquals("new-token", session.getToken());
        assertEquals("new-refresh-token", session.getRefreshToken());
        assertFalse(session.isExpired());
    }

    @Test
    @DisplayName("Login log should record success")
    void loginLogSuccess() {
        UUID userId = UUID.randomUUID();
        LoginLog log = LoginLog.success(userId, "doctor1", "192.168.1.1", "Mozilla/5.0");

        assertEquals(userId, log.getUserId());
        assertEquals("doctor1", log.getUsername());
        assertEquals("192.168.1.1", log.getIpAddress());
        assertEquals(LoginLog.LoginStatus.SUCCESS, log.getStatus());
        assertNull(log.getFailureReason());
        assertNotNull(log.getLoginAt());
    }

    @Test
    @DisplayName("Login log should record failure with reason")
    void loginLogFailure() {
        LoginLog log = LoginLog.failure(
                "hacker",
                "10.0.0.99",
                "Unknown",
                LoginLog.LoginStatus.FAILED_INVALID_CREDENTIALS,
                "Sai mật khẩu"
        );

        assertEquals("hacker", log.getUsername());
        assertEquals("10.0.0.99", log.getIpAddress());
        assertEquals(LoginLog.LoginStatus.FAILED_INVALID_CREDENTIALS, log.getStatus());
        assertEquals("Sai mật khẩu", log.getFailureReason());
    }

    @Test
    @DisplayName("Login log should support all failure statuses")
    void loginLogAllStatuses() {
        for (LoginLog.LoginStatus status : LoginLog.LoginStatus.values()) {
            LoginLog log = LoginLog.failure("user", "ip", "agent", status, status.name());
            assertEquals(status, log.getStatus());
        }
    }
}
