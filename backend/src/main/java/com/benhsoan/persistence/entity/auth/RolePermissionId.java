package com.benhsoan.persistence.entity.auth;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RolePermissionId implements Serializable {

    private UUID roleId;

    private UUID permissionId;
}