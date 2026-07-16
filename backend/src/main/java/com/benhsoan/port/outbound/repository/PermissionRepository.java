package com.benhsoan.port.outbound.repository;

import com.benhsoan.domain.auth.Permission;

import java.util.List;

public interface PermissionRepository {
    List<Permission> findAll();
    List<Permission> findByRoleId(java.util.UUID roleId);
}
