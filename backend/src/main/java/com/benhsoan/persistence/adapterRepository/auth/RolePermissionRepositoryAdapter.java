package com.benhsoan.persistence.adapterRepository.auth;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.benhsoan.domain.auth.RolePermission;
import com.benhsoan.persistence.entity.auth.RolePermissionEntity;
import com.benhsoan.persistence.jpaRepository.auth.JpaRolePermissionRepository;
import com.benhsoan.persistence.mapper.auth.RolePermissionPersistenceMapper;
import com.benhsoan.port.outbound.repository.crudRepository.auth.RolePermissionRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RolePermissionRepositoryAdapter
        implements RolePermissionRepository {

    private final JpaRolePermissionRepository jpaRepository;
    private final RolePermissionPersistenceMapper mapper;

    @Override
    public RolePermission save(RolePermission rolePermission) {

        RolePermissionEntity entity = mapper.toEntity(rolePermission);

        return mapper.toDomain(
                jpaRepository.save(entity)
        );
    }

    @Override
    public List<RolePermission> findByRoleId(UUID roleId) {

        return jpaRepository.findByRoleId(roleId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<RolePermission> findByPermissionId(UUID permissionId) {

        return jpaRepository.findByPermissionId(permissionId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByRoleIdAndPermissionId(
            UUID roleId,
            UUID permissionId) {

        return jpaRepository.existsByRoleIdAndPermissionId(
                roleId,
                permissionId);
    }

    @Override
    public void deleteByRoleIdAndPermissionId(
            UUID roleId,
            UUID permissionId) {

        jpaRepository.deleteByRoleIdAndPermissionId(
                roleId,
                permissionId);
    }
}