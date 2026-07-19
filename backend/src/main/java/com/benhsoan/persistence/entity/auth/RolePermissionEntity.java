package com.benhsoan.persistence.entity.auth;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role_permissions")
@IdClass(RolePermissionId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermissionEntity {

    @Id
    @Column(name = "role_id", columnDefinition = "BINARY(16)")
    private UUID roleId;

    @Id
    @Column(name = "permission_id", columnDefinition = "BINARY(16)")
    private UUID permissionId;
}