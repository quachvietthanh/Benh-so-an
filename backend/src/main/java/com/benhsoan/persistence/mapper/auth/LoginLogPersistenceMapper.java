package com.benhsoan.persistence.mapper.auth;

import org.springframework.stereotype.Component;

import com.benhsoan.domain.auth.LoginLog;
import com.benhsoan.persistence.entity.auth.LoginLogEntity;

@Component
public class LoginLogPersistenceMapper {

    public LoginLog toDomain(LoginLogEntity entity) {

        if (entity == null) {
            return null;
        }

        return LoginLog.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getActionType(),
                entity.getResourceType(),
                entity.getResourceId(),
                entity.getDetail(),
                entity.getIpAddress(),
                entity.getCreatedAt()
        );
    }

    public LoginLogEntity toEntity(LoginLog domain) {

        if (domain == null) {
            return null;
        }

        return LoginLogEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .actionType(domain.getActionType())
                .resourceType(domain.getResourceType())
                .resourceId(domain.getResourceId())
                .detail(domain.getDetail())
                .ipAddress(domain.getIpAddress())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}