package com.benhsoan.port.outbound.repository.crudRepository.auth;

import java.util.Optional;
import java.util.UUID;

import com.benhsoan.domain.auth.UserSession;
import com.benhsoan.port.outbound.repository.BaseRepository;

public interface UserSessionRepository extends BaseRepository<UserSession, UUID> {

    Optional<UserSession> findByUserId(UUID userId);

    Optional<UserSession> findByTokenHash(String tokenHash);

    boolean existsByTokenHash(String tokenHash);
    
    void deleteExpiredSessions();

    void deleteByUserId(UUID userId);
}
