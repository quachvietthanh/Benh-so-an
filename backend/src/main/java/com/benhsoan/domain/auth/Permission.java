package com.benhsoan.domain.auth;

import java.util.Objects;
import java.util.UUID;

import com.benhsoan.domain.auth.enums.PermissionAction;
import com.benhsoan.domain.shared.Guard.Guard;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Permission {

    private UUID id;

    private String featureCode;

    private PermissionAction action;

    private String description;

    private Permission(
            UUID id,
            String featureCode,
            PermissionAction action,
            String description
    ) {
        this.id = Objects.requireNonNull(id);
        this.featureCode = Guard.require(featureCode, "Feature code");
        this.action = Guard.require(action, "Permission action");
        this.description = description;
    }

    public static Permission create(
            String featureCode,
            PermissionAction action,
            String description
    ) {
        return new Permission(
                UUID.randomUUID(),
                featureCode,
                action,
                description
        );
    }

    public void updateDescription(String description){
        this.description = description;
    }
}