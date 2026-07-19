package com.benhsoan.persistence.mapper;

import org.springframework.stereotype.Component;

import com.benhsoan.domain.auth.Role;
import com.benhsoan.persistence.entity.RoleEntity;

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
                entity.getCreatedAt()
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
                .build();
    }
}