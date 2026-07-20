package com.benhsoan.persistence.adapterRepository.auth;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.benhsoan.domain.auth.UserSession;
import com.benhsoan.persistence.entity.auth.UserSessionEntity;
import com.benhsoan.persistence.jpaRepository.auth.JpaUserSessionRepository;
import com.benhsoan.persistence.mapper.auth.UserSessionPersistenceMapper;
import com.benhsoan.port.outbound.repository.crudRepository.auth.UserSessionRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserSessionRepositoryAdapter implements UserSessionRepository {

    private final JpaUserSessionRepository jpaRepository;

    private final UserSessionPersistenceMapper mapper;

    @Override
    public Optional<UserSession> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<UserSession> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<UserSession> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }

    @Override
    public UserSession save(UserSession session) {

        UserSessionEntity entity = mapper.toEntity(session);

        UserSessionEntity saved = jpaRepository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByTokenHash(String tokenHash) {
        return jpaRepository.existsByTokenHash(tokenHash);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteExpiredSessions() {
        jpaRepository.deleteByExpiresAtBefore(Instant.now());
    }
}