package com.benhsoan.infrastructure.security.service;

import com.benhsoan.domain.auth.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PermissionEvaluator {

    private static final String ROLE_PREFIX = "ROLE_";

    /**
     * Map role to permissions (static mapping based on system design).
     * In production, this would be loaded from database.
     */
    private static final java.util.Map<String, Set<Permission>> ROLE_PERMISSIONS = java.util.Map.of(
            "ROLE_ADMIN", Set.of(Permission.values()),
            "ROLE_DOCTOR", Set.of(
                    Permission.PATIENT_CREATE, Permission.PATIENT_READ, Permission.PATIENT_UPDATE,
                    Permission.RECORD_CREATE, Permission.RECORD_READ, Permission.RECORD_UPDATE, Permission.RECORD_UPDATE_STATUS,
                    Permission.PRESCRIPTION_CREATE, Permission.PRESCRIPTION_READ, Permission.PRESCRIPTION_UPDATE, Permission.PRESCRIPTION_DELETE,
                    Permission.APPOINTMENT_CREATE, Permission.APPOINTMENT_READ, Permission.APPOINTMENT_UPDATE,
                    Permission.VITAL_SIGN_CREATE, Permission.VITAL_SIGN_READ, Permission.VITAL_SIGN_UPDATE,
                    Permission.DIAGNOSIS_CREATE, Permission.DIAGNOSIS_READ, Permission.DIAGNOSIS_UPDATE
            ),
            "ROLE_NURSE", Set.of(
                    Permission.PATIENT_READ,
                    Permission.RECORD_READ, Permission.RECORD_UPDATE_STATUS,
                    Permission.APPOINTMENT_READ,
                    Permission.VITAL_SIGN_CREATE, Permission.VITAL_SIGN_READ, Permission.VITAL_SIGN_UPDATE
            ),
            "ROLE_RECEPTIONIST", Set.of(
                    Permission.PATIENT_CREATE, Permission.PATIENT_READ, Permission.PATIENT_UPDATE,
                    Permission.APPOINTMENT_CREATE, Permission.APPOINTMENT_READ, Permission.APPOINTMENT_UPDATE, Permission.APPOINTMENT_DELETE,
                    Permission.INVOICE_CREATE, Permission.INVOICE_READ, Permission.INVOICE_UPDATE
            ),
            "ROLE_PHARMACIST", Set.of(
                    Permission.PRESCRIPTION_READ, Permission.PRESCRIPTION_UPDATE_STATUS,
                    Permission.PHARMACY_CREATE, Permission.PHARMACY_READ, Permission.PHARMACY_UPDATE
            )
    );

    public Set<Permission> getCurrentUserPermissions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Set.of();
        }

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(ROLE_PREFIX))
                .map(ROLE_PERMISSIONS::get)
                .filter(permissions -> permissions != null)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public List<String> getCurrentUserRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return List.of();
        }

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(ROLE_PREFIX))
                .map(authority -> authority.substring(ROLE_PREFIX.length()))
                .collect(Collectors.toList());
    }

    public boolean hasPermission(Permission permission) {
        return getCurrentUserPermissions().contains(permission);
    }

    public boolean hasAnyPermission(Permission... permissions) {
        Set<Permission> userPermissions = getCurrentUserPermissions();
        return Arrays.stream(permissions).anyMatch(userPermissions::contains);
    }

    public boolean hasAllPermissions(Permission... permissions) {
        Set<Permission> userPermissions = getCurrentUserPermissions();
        return userPermissions.containsAll(Set.of(permissions));
    }

    public boolean hasRole(String role) {
        return getCurrentUserRoles().contains(role);
    }

    public boolean hasAnyRole(String... roles) {
        List<String> userRoles = getCurrentUserRoles();
        return Arrays.stream(roles).anyMatch(userRoles::contains);
    }
}
