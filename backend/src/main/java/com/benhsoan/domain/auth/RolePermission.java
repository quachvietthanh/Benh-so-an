package com.benhsoan.domain.auth;

import java.util.UUID;

import com.benhsoan.domain.shared.Guard.Guard;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RolePermission {

    private UUID roleId;

    private UUID permissionId;

    private RolePermission(
            UUID roleId,
            UUID permissionId
    ) {
        this.roleId = Guard.require(roleId, "Role id");
        this.permissionId = Guard.require(permissionId, "Permission id");
    }

    public static RolePermission create(
            UUID roleId,
            UUID permissionId
    ) {
        return new RolePermission(
                roleId,
                permissionId
        );
    }

    public static RolePermission restore(
        UUID roleId,
        UUID permissionId
) {
    return new RolePermission(
            roleId,
            permissionId
    );
}
}