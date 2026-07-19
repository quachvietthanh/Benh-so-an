package com.benhsoan.port.outbound.repository.crudRepository.auth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.benhsoan.domain.auth.Permission;
import com.benhsoan.domain.auth.enums.PermissionAction;
import com.benhsoan.port.outbound.repository.BaseRepository;

public interface PermissionRepository extends BaseRepository<Permission, UUID> {

    Optional<Permission> findByFeatureCodeAndAction(
            String featureCode,
            PermissionAction action);

    List<Permission> findByFeatureCode(String featureCode);

    boolean existsByFeatureCodeAndAction(
            String featureCode,
            PermissionAction action);
}