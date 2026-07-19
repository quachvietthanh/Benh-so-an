package com.benhsoan.infrastructure.persistence.repository;

import com.benhsoan.infrastructure.persistence.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserSessionRepository extends JpaRepository<UserSessionEntity, UUID> {
    Optional<UserSessionEntity> findByUserId(UUID userId);
    Optional<UserSessionEntity> findByTokenHash(String tokenHash);
    boolean existsByTokenHash(String tokenHash);
    void deleteByUserId(UUID userId);
}
