package com.benhsoan.domain.auth;

import java.time.Instant;
import java.util.UUID;

import com.benhsoan.domain.shared.Guard.Guard;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString(exclude = "tokenHash")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSession {

    private UUID id;

    private UUID userId;

    private String tokenHash;

    private Instant expiresAt;

    private Instant createdAt;

    private Instant lastUsedAt;

    private Instant revokedAt;

    private UserSession(
            UUID id,
            UUID userId,
            String tokenHash,
            Instant expiresAt,
            Instant createdAt,
            Instant lastUsedAt,
            Instant revokedAt
    ) {
        this.id = Guard.require(id, "Session id");
        this.userId = Guard.require(userId, "User id");
        this.tokenHash = Guard.require(tokenHash, "Token hash");
        this.expiresAt = Guard.require(expiresAt, "Expires at");
        this.createdAt = Guard.require(createdAt, "Created at");

        this.lastUsedAt = lastUsedAt;
        this.revokedAt = revokedAt;
    }

    public static UserSession create(
            UUID userId,
            String tokenHash,
            Instant expiresAt
    ) {
        Instant now = Instant.now();

        return new UserSession(
                UUID.randomUUID(),
                userId,
                tokenHash,
                expiresAt,
                now,
                now,
                null
        );
    }

    public void updateLastUsed() {
        this.lastUsedAt = Instant.now();
    }

    public void revoke() {
        this.revokedAt = Instant.now();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isActive() {
        return !isExpired() && !isRevoked();
    }
}