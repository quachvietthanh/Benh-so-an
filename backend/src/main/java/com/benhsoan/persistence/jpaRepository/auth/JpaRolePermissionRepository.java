package com.benhsoan.persistence.jpaRepository.auth;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benhsoan.persistence.entity.auth.RolePermissionEntity;
import com.benhsoan.persistence.entity.auth.RolePermissionId;

public interface JpaRolePermissionRepository
        extends JpaRepository<RolePermissionEntity, RolePermissionId> {

    List<RolePermissionEntity> findByRoleId(UUID roleId);

    List<RolePermissionEntity> findByPermissionId(UUID permissionId);

    boolean existsByRoleIdAndPermissionId(
            UUID roleId,
            UUID permissionId);

    void deleteByRoleIdAndPermissionId(
            UUID roleId,
            UUID permissionId);
}