package com.benhsoan.persistence.mapper;

import org.springframework.stereotype.Component;

import com.benhsoan.domain.auth.RolePermission;
import com.benhsoan.persistence.entity.RolePermissionEntity;

@Component
public class RolePermissionPersistenceMapper {

    public RolePermission toDomain(RolePermissionEntity entity) {

        if (entity == null) {
            return null;
        }

        return RolePermission.restore(
                entity.getRoleId(),
                entity.getPermissionId()
        );
    }

    public RolePermissionEntity toEntity(RolePermission domain) {

        if (domain == null) {
            return null;
        }

        return RolePermissionEntity.builder()
                .roleId(domain.getRoleId())
                .permissionId(domain.getPermissionId())
                .build();
    }
}