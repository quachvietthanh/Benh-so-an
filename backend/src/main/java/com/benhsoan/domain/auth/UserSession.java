package com.benhsoan.domain.auth;

import java.time.Duration;
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

    public void updateLastUsed(Instant now) {
    this.lastUsedAt = now;
}

public void revoke(Instant now) {
    this.revokedAt = now;
}

public boolean isExpired(Instant now) {
    return now.isAfter(expiresAt);
}

public boolean isIdleTimeout(
        Instant now,
        Duration timeout
) {
    return now.isAfter(lastUsedAt.plus(timeout));
}

public boolean isActive(
        Instant now,
        Duration timeout
) {
    return !isExpired(now)
            && !isRevoked()
            && !isIdleTimeout(now, timeout);
}
    public boolean isRevoked() {
        return revokedAt != null;
    }


    public void refresh(Duration timeout) {
        Instant now = Instant.now();
        this.lastUsedAt = now;
        this.expiresAt = now.plus(timeout);
    }



    public static UserSession restore(
        UUID id,
        UUID userId,
        String tokenHash,
        Instant expiresAt,
        Instant createdAt,
        Instant lastUsedAt,
        Instant revokedAt
) {
    return new UserSession(
            id,
            userId,
            tokenHash,
            expiresAt,
            createdAt,
            lastUsedAt,
            revokedAt
    );
}
}
