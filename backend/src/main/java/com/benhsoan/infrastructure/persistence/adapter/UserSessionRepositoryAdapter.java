package com.benhsoan.infrastructure.persistence.adapter;

import com.benhsoan.domain.auth.UserSession;
import com.benhsoan.infrastructure.persistence.entity.UserSessionEntity;
import com.benhsoan.infrastructure.persistence.repository.JpaUserSessionRepository;
import com.benhsoan.port.outbound.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserSessionRepositoryAdapter implements UserSessionRepository {

    private final JpaUserSessionRepository jpaUserSessionRepository;

    @Override
    public Optional<UserSession> findById(UUID id) {
        return jpaUserSessionRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<UserSession> findByUserId(UUID userId) {
        return jpaUserSessionRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public Optional<UserSession> findByTokenHash(String tokenHash) {
        return jpaUserSessionRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public boolean existsByTokenHash(String tokenHash) {
        return jpaUserSessionRepository.existsByTokenHash(tokenHash);
    }

    @Override
    public UserSession save(UserSession session) {
        UserSessionEntity entity = toEntity(session);
        UserSessionEntity saved = jpaUserSessionRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        jpaUserSessionRepository.deleteById(id);
    }

    @Override
    public void deleteExpiredSessions() {
        jpaUserSessionRepository.deleteAllById(
            jpaUserSessionRepository.findAll().stream()
                .filter(e -> e.getExpiresAt().isBefore(Instant.now()))
                .map(UserSessionEntity::getId)
                .toList()
        );
    }

    @Override
    public void deleteByUserId(UUID userId) {
        jpaUserSessionRepository.deleteByUserId(userId);
    }

    private UserSession toDomain(UserSessionEntity entity) {
        return UserSession.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getLastUsedAt() != null ? entity.getLastUsedAt() : entity.getCreatedAt(),
                entity.getRevokedAt()
        );
    }

    private UserSessionEntity toEntity(UserSession domain) {
        return UserSessionEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .tokenHash(domain.getTokenHash())
                .expiresAt(domain.getExpiresAt())
                .createdAt(domain.getCreatedAt())
                .lastUsedAt(domain.getLastUsedAt())
                .revokedAt(domain.getRevokedAt())
                .build();
    }
}
