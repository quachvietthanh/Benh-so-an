package com.benhsoan.persistence.adapterRepository.auth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.benhsoan.domain.auth.LoginLog;
import com.benhsoan.persistence.entity.auth.LoginLogEntity;
import com.benhsoan.persistence.jpaRepository.auth.JpaLoginLogRepository;
import com.benhsoan.persistence.mapper.auth.LoginLogPersistenceMapper;
import com.benhsoan.port.outbound.repository.logRepository.LoginLogRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LoginLogRepositoryAdapter
        implements LoginLogRepository {

    private final JpaLoginLogRepository jpaRepository;
    private final LoginLogPersistenceMapper mapper;

    @Override
    public Optional<LoginLog> findById(UUID id) {

        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<LoginLog> findByUserId(UUID userId) {

        return jpaRepository.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public LoginLog save(LoginLog loginLog) {

        LoginLogEntity entity = mapper.toEntity(loginLog);

        return mapper.toDomain(
                jpaRepository.save(entity)
        );
    }

    @Override
    public void deleteById(UUID id) {

        jpaRepository.deleteById(id);
    }
}