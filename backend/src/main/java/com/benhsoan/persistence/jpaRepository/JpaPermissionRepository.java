package com.benhsoan.persistence.jpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benhsoan.domain.auth.enums.PermissionAction;
import com.benhsoan.persistence.entity.PermissionEntity;

public interface JpaPermissionRepository extends JpaRepository<PermissionEntity, UUID> {

    Optional<PermissionEntity> findByFeatureCodeAndAction(
            String featureCode,
            PermissionAction action
    );

    List<PermissionEntity> findByFeatureCode(String featureCode);

    boolean existsByFeatureCodeAndAction(
            String featureCode,
            PermissionAction action
    );
}