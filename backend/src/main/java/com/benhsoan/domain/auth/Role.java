package com.benhsoan.domain.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Role {

    private UUID id;

    private String name;

    private String description;

    @Builder.Default
    private Set<Permission> permissions = Set.of();

    private boolean isSystem;

    private Instant createdAt;

    private Instant updatedAt;

    public void addPermission(Permission permission) {
        this.permissions = new java.util.HashSet<>(this.permissions);
        this.permissions.add(permission);
        this.permissions = Set.copyOf(this.permissions);
    }

    public void removePermission(Permission permission) {
        this.permissions = new java.util.HashSet<>(this.permissions);
        this.permissions.remove(permission);
        this.permissions = Set.copyOf(this.permissions);
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public boolean hasAnyPermission(Permission... permissions) {
        return Set.of(permissions).stream().anyMatch(this.permissions::contains);
    }

    public boolean hasAllPermissions(Permission... permissions) {
        return this.permissions.containsAll(Set.of(permissions));
    }

    public static Role admin() {
        return Role.builder()
                .name("ADMIN")
                .description("Quản trị viên hệ thống")
                .permissions(Set.of(Permission.values()))
                .isSystem(true)
                .build();
    }

    public static Role doctor() {
        return Role.builder()
                .name("DOCTOR")
                .description("Bác sĩ")
                .permissions(Set.of(
                        Permission.PATIENT_CREATE, Permission.PATIENT_READ, Permission.PATIENT_UPDATE,
                        Permission.RECORD_CREATE, Permission.RECORD_READ, Permission.RECORD_UPDATE, Permission.RECORD_UPDATE_STATUS,
                        Permission.PRESCRIPTION_CREATE, Permission.PRESCRIPTION_READ, Permission.PRESCRIPTION_UPDATE, Permission.PRESCRIPTION_DELETE,
                        Permission.APPOINTMENT_CREATE, Permission.APPOINTMENT_READ, Permission.APPOINTMENT_UPDATE,
                        Permission.VITAL_SIGN_CREATE, Permission.VITAL_SIGN_READ, Permission.VITAL_SIGN_UPDATE,
                        Permission.DIAGNOSIS_CREATE, Permission.DIAGNOSIS_READ, Permission.DIAGNOSIS_UPDATE
                ))
                .isSystem(true)
                .build();
    }

    public static Role nurse() {
        return Role.builder()
                .name("NURSE")
                .description("Y tá")
                .permissions(Set.of(
                        Permission.PATIENT_READ,
                        Permission.RECORD_READ, Permission.RECORD_UPDATE_STATUS,
                        Permission.APPOINTMENT_READ,
                        Permission.VITAL_SIGN_CREATE, Permission.VITAL_SIGN_READ, Permission.VITAL_SIGN_UPDATE
                ))
                .isSystem(true)
                .build();
    }

    public static Role receptionist() {
        return Role.builder()
                .name("RECEPTIONIST")
                .description("Lễ tân")
                .permissions(Set.of(
                        Permission.PATIENT_CREATE, Permission.PATIENT_READ, Permission.PATIENT_UPDATE,
                        Permission.APPOINTMENT_CREATE, Permission.APPOINTMENT_READ, Permission.APPOINTMENT_UPDATE, Permission.APPOINTMENT_DELETE,
                        Permission.INVOICE_CREATE, Permission.INVOICE_READ, Permission.INVOICE_UPDATE
                ))
                .isSystem(true)
                .build();
    }

    public static Role pharmacist() {
        return Role.builder()
                .name("PHARMACIST")
                .description("Dược sĩ")
                .permissions(Set.of(
                        Permission.PRESCRIPTION_READ, Permission.PRESCRIPTION_UPDATE_STATUS,
                        Permission.PHARMACY_CREATE, Permission.PHARMACY_READ, Permission.PHARMACY_UPDATE
                ))
                .isSystem(true)
                .build();
    }
}
