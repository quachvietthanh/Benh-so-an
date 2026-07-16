package com.benhsoan.infrastructure.security;

import com.benhsoan.domain.auth.Permission;
import com.benhsoan.infrastructure.security.service.PermissionEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PermissionEvaluator Tests")
class PermissionEvaluatorTest {

    private PermissionEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new PermissionEvaluator();
    }

    @Test
    @DisplayName("Admin should have all permissions")
    void adminHasAllPermissions() {
        authenticateAs("admin", "ADMIN");
        Permission[] allPermissions = Permission.values();
        assertTrue(evaluator.hasAllPermissions(allPermissions));
        assertTrue(evaluator.hasPermission(Permission.USER_CREATE));
        assertTrue(evaluator.hasPermission(Permission.AUDIT_READ));
    }

    @Test
    @DisplayName("Doctor should have medical permissions but not admin permissions")
    void doctorHasMedicalPermissions() {
        authenticateAs("doctor", "DOCTOR");
        assertTrue(evaluator.hasPermission(Permission.PATIENT_CREATE));
        assertTrue(evaluator.hasPermission(Permission.RECORD_CREATE));
        assertTrue(evaluator.hasPermission(Permission.PRESCRIPTION_CREATE));
        assertTrue(evaluator.hasPermission(Permission.DIAGNOSIS_CREATE));

        assertFalse(evaluator.hasPermission(Permission.USER_CREATE));
        assertFalse(evaluator.hasPermission(Permission.AUDIT_READ));
        assertFalse(evaluator.hasPermission(Permission.ROLE_CREATE));
    }

    @Test
    @DisplayName("Nurse should have limited read permissions")
    void nurseHasLimitedPermissions() {
        authenticateAs("nurse", "NURSE");
        assertTrue(evaluator.hasPermission(Permission.PATIENT_READ));
        assertTrue(evaluator.hasPermission(Permission.RECORD_READ));
        assertTrue(evaluator.hasPermission(Permission.VITAL_SIGN_CREATE));

        assertFalse(evaluator.hasPermission(Permission.PATIENT_CREATE));
        assertFalse(evaluator.hasPermission(Permission.RECORD_CREATE));
        assertFalse(evaluator.hasPermission(Permission.PRESCRIPTION_READ));
    }

    @Test
    @DisplayName("Receptionist should have appointment and invoice permissions")
    void receptionistHasAppointmentPermissions() {
        authenticateAs("receptionist", "RECEPTIONIST");
        assertTrue(evaluator.hasPermission(Permission.PATIENT_CREATE));
        assertTrue(evaluator.hasPermission(Permission.APPOINTMENT_CREATE));
        assertTrue(evaluator.hasPermission(Permission.INVOICE_CREATE));

        assertFalse(evaluator.hasPermission(Permission.RECORD_READ));
        assertFalse(evaluator.hasPermission(Permission.PRESCRIPTION_READ));
        assertFalse(evaluator.hasPermission(Permission.VITAL_SIGN_READ));
    }

    @Test
    @DisplayName("Pharmacist should have pharmacy permissions")
    void pharmacistHasPharmacyPermissions() {
        authenticateAs("pharmacist", "PHARMACIST");
        assertTrue(evaluator.hasPermission(Permission.PRESCRIPTION_READ));
        assertTrue(evaluator.hasPermission(Permission.PRESCRIPTION_UPDATE_STATUS));
        assertTrue(evaluator.hasPermission(Permission.PHARMACY_CREATE));
        assertTrue(evaluator.hasPermission(Permission.PHARMACY_READ));
        assertTrue(evaluator.hasPermission(Permission.PHARMACY_UPDATE));

        assertFalse(evaluator.hasPermission(Permission.PATIENT_READ));
        assertFalse(evaluator.hasPermission(Permission.RECORD_READ));
        assertFalse(evaluator.hasPermission(Permission.APPOINTMENT_READ));
    }

    @Test
    @DisplayName("No authentication should return empty permissions")
    void noAuthReturnsEmpty() {
        SecurityContextHolder.clearContext();
        assertTrue(evaluator.getCurrentUserPermissions().isEmpty());
        assertFalse(evaluator.hasPermission(Permission.PATIENT_READ));
        assertFalse(evaluator.hasAnyPermission(Permission.PATIENT_READ, Permission.USER_CREATE));
    }

    @Test
    @DisplayName("hasAnyRole should work correctly")
    void hasAnyRoleWorks() {
        authenticateAs("admin", "ADMIN");
        assertTrue(evaluator.hasAnyRole("ADMIN", "DOCTOR"));
        assertTrue(evaluator.hasRole("ADMIN"));
        assertFalse(evaluator.hasRole("DOCTOR"));
    }

    private void authenticateAs(String username, String role) {
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                )
        );
    }
}
