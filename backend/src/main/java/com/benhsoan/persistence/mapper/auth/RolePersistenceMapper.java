package com.benhsoan.persistence.mapper.auth;

import java.util.HashSet;

import org.springframework.stereotype.Component;

import com.benhsoan.domain.auth.Role;
import com.benhsoan.persistence.entity.auth.RoleEntity;

@Component
public class RolePersistenceMapper {

    public Role toDomain(RoleEntity entity) {
        if (entity == null) {
            return null;
        }

        return Role.restore(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isSystem(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getPermissions()
        );
    }

    public RoleEntity toEntity(Role domain) {
        if (domain == null) {
            return null;
        }

        return RoleEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .isSystem(domain.isSystem())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .permissions(new HashSet<>(domain.getPermissions()))
                .build();
    }
}