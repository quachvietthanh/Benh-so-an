package com.benhsoan.port.outbound.repository;

import java.util.Optional;
import java.util.UUID;

import com.benhsoan.domain.auth.UserSession;

public interface UserSessionRepository extends BaseRepository<UserSession, UUID> {

    Optional<UserSession> findByUserId(UUID userId);

    Optional<UserSession> findByTokenHash(String tokenHash);

    boolean existsByTokenHash(String tokenHash);
}
