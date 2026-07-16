package com.benhsoan.persistence.mapper;

import org.springframework.stereotype.Component;

import com.benhsoan.domain.auth.Permission;
import com.benhsoan.persistence.entity.PermissionEntity;

@Component
public class PermissionPersistenceMapper {

    public Permission toDomain(PermissionEntity entity) {

        if (entity == null) {
            return null;
        }

        return Permission.restore(
                entity.getId(),
                entity.getFeatureCode(),
                entity.getAction(),
                entity.getDescription()
        );
    }

    public PermissionEntity toEntity(Permission domain) {

        if (domain == null) {
            return null;
        }

        return PermissionEntity.builder()
                .id(domain.getId())
                .featureCode(domain.getFeatureCode())
                .action(domain.getAction())
                .description(domain.getDescription())
                .build();
    }
}