package com.benhsoan.persistence.jpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benhsoan.persistence.entity.UserSessionEntity;

public interface JpaUserSessionRepository extends JpaRepository<UserSessionEntity, UUID> {

    Optional<UserSessionEntity> findByTokenHash(String tokenHash);

    Optional<UserSessionEntity> findByUserId(UUID userId);

    boolean existsByTokenHash(String tokenHash);

    void deleteByUserId(UUID userId);

    void deleteByExpiresAtBefore(Instant time);
}