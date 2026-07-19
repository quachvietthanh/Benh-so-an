package com.benhsoan.port.outbound.repository.crudRepository.auth;

import java.util.List;
import java.util.UUID;

import com.benhsoan.domain.auth.RolePermission;

public interface RolePermissionRepository {

    RolePermission save(RolePermission rolePermission);

    List<RolePermission> findByRoleId(UUID roleId);

    List<RolePermission> findByPermissionId(UUID permissionId);

    boolean existsByRoleIdAndPermissionId(
            UUID roleId,
            UUID permissionId);

    void deleteByRoleIdAndPermissionId(
            UUID roleId,
            UUID permissionId);
}