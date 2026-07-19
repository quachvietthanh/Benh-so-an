package com.benhsoan.persistence.adapterRepository.auth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.benhsoan.domain.auth.Permission;
import com.benhsoan.domain.auth.enums.PermissionAction;
import com.benhsoan.persistence.entity.auth.PermissionEntity;
import com.benhsoan.persistence.jpaRepository.auth.JpaPermissionRepository;
import com.benhsoan.persistence.mapper.auth.PermissionPersistenceMapper;
import com.benhsoan.port.outbound.repository.crudRepository.auth.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final JpaPermissionRepository jpaRepository;

    private final PermissionPersistenceMapper mapper;

    @Override
    public Optional<Permission> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Permission> findByFeatureCodeAndAction(
            String featureCode,
            PermissionAction action
    ) {
        return jpaRepository.findByFeatureCodeAndAction(featureCode, action)
                .map(mapper::toDomain);
    }

    @Override
    public List<Permission> findByFeatureCode(String featureCode) {
        return jpaRepository.findByFeatureCode(featureCode)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Permission save(Permission permission) {

        PermissionEntity entity = mapper.toEntity(permission);

        PermissionEntity saved = jpaRepository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsByFeatureCodeAndAction(
            String featureCode,
            PermissionAction action
    ) {
        return jpaRepository.existsByFeatureCodeAndAction(featureCode, action);
    }

    @Override
    public void deleteById(UUID id) {
    jpaRepository.deleteById(id);
}
}