package com.benhsoan.domain.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSession {

    private UUID id;

    private UUID userId;

    private String token;

    private String refreshToken;

    private String ipAddress;

    private String userAgent;

    private Instant loginAt;

    private Instant lastActiveAt;

    private Instant expiresAt;

    private boolean active;

    public void refresh(String newToken, String newRefreshToken, Instant expiresAt) {
        this.token = newToken;
        this.refreshToken = newRefreshToken;
        this.expiresAt = expiresAt;
        this.lastActiveAt = Instant.now();
    }

    public void markInactive() {
        this.active = false;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
