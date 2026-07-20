// package com.benhsoan.domain.auth;

// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;

// import java.util.Set;

// import static org.junit.jupiter.api.Assertions.*;

// @DisplayName("Role-Permission Mapping Tests")
// class RolePermissionTest {

//     @Test
//     @DisplayName("Admin should have all permissions")
//     void adminHasAllPermissions() {
//         Role admin = Role.admin();
//         assertTrue(admin.hasPermission(Permission.USER_CREATE));
//         assertTrue(admin.hasPermission(Permission.PATIENT_CREATE));
//         assertTrue(admin.hasPermission(Permission.RECORD_CREATE));
//         assertTrue(admin.hasPermission(Permission.PRESCRIPTION_CREATE));
//         assertTrue(admin.hasPermission(Permission.APPOINTMENT_CREATE));
//         assertTrue(admin.hasPermission(Permission.VITAL_SIGN_CREATE));
//         assertTrue(admin.hasPermission(Permission.DIAGNOSIS_CREATE));
//         assertTrue(admin.hasPermission(Permission.PHARMACY_CREATE));
//         assertTrue(admin.hasPermission(Permission.INVOICE_CREATE));
//         assertTrue(admin.hasPermission(Permission.AUDIT_READ));
//         assertTrue(admin.hasPermission(Permission.ROLE_READ));
//         assertTrue(admin.hasPermission(Permission.PERMISSION_READ));
//     }

//     @Test
//     @DisplayName("Doctor should have medical-related permissions")
//     void doctorHasMedicalPermissions() {
//         Role doctor = Role.doctor();
//         assertTrue(doctor.hasPermission(Permission.PATIENT_CREATE));
//         assertTrue(doctor.hasPermission(Permission.PATIENT_READ));
//         assertTrue(doctor.hasPermission(Permission.RECORD_CREATE));
//         assertTrue(doctor.hasPermission(Permission.RECORD_READ));
//         assertTrue(doctor.hasPermission(Permission.PRESCRIPTION_CREATE));
//         assertTrue(doctor.hasPermission(Permission.PRESCRIPTION_READ));
//         assertTrue(doctor.hasPermission(Permission.DIAGNOSIS_CREATE));
        
//         assertFalse(doctor.hasPermission(Permission.USER_CREATE));
//         assertFalse(doctor.hasPermission(Permission.PATIENT_DELETE));
//         assertFalse(doctor.hasPermission(Permission.RECORD_DELETE));
//         assertFalse(doctor.hasPermission(Permission.PHARMACY_CREATE));
//         assertFalse(doctor.hasPermission(Permission.INVOICE_CREATE));
//         assertFalse(doctor.hasPermission(Permission.AUDIT_READ));
//         assertFalse(doctor.hasPermission(Permission.ROLE_CREATE));
//     }

//     @Test
//     @DisplayName("Nurse should have read-only and vital sign permissions")
//     void nurseHasLimitedPermissions() {
//         Role nurse = Role.nurse();
//         assertTrue(nurse.hasPermission(Permission.PATIENT_READ));
//         assertTrue(nurse.hasPermission(Permission.RECORD_READ));
//         assertTrue(nurse.hasPermission(Permission.VITAL_SIGN_CREATE));
//         assertTrue(nurse.hasPermission(Permission.VITAL_SIGN_READ));
        
//         assertFalse(nurse.hasPermission(Permission.PATIENT_CREATE));
//         assertFalse(nurse.hasPermission(Permission.RECORD_CREATE));
//         assertFalse(nurse.hasPermission(Permission.PRESCRIPTION_CREATE));
//         assertFalse(nurse.hasPermission(Permission.DIAGNOSIS_CREATE));
//         assertFalse(nurse.hasPermission(Permission.INVOICE_CREATE));
//         assertFalse(nurse.hasPermission(Permission.PHARMACY_READ));
//         assertFalse(nurse.hasPermission(Permission.USER_READ));
//     }

//     @Test
//     @DisplayName("Receptionist should have patient and appointment permissions")
//     void receptionistHasAppointmentPermissions() {
//         Role receptionist = Role.receptionist();
//         assertTrue(receptionist.hasPermission(Permission.PATIENT_CREATE));
//         assertTrue(receptionist.hasPermission(Permission.PATIENT_READ));
//         assertTrue(receptionist.hasPermission(Permission.APPOINTMENT_CREATE));
//         assertTrue(receptionist.hasPermission(Permission.APPOINTMENT_READ));
//         assertTrue(receptionist.hasPermission(Permission.INVOICE_CREATE));
//         assertTrue(receptionist.hasPermission(Permission.INVOICE_READ));
        
//         assertFalse(receptionist.hasPermission(Permission.RECORD_READ));
//         assertFalse(receptionist.hasPermission(Permission.PRESCRIPTION_READ));
//         assertFalse(receptionist.hasPermission(Permission.VITAL_SIGN_READ));
//         assertFalse(receptionist.hasPermission(Permission.DIAGNOSIS_READ));
//         assertFalse(receptionist.hasPermission(Permission.PHARMACY_READ));
//     }

//     @Test
//     @DisplayName("Pharmacist should have pharmacy and prescription permissions")
//     void pharmacistHasPharmacyPermissions() {
//         Role pharmacist = Role.pharmacist();
//         assertTrue(pharmacist.hasPermission(Permission.PRESCRIPTION_READ));
//         assertTrue(pharmacist.hasPermission(Permission.PRESCRIPTION_UPDATE_STATUS));
//         assertTrue(pharmacist.hasPermission(Permission.PHARMACY_CREATE));
//         assertTrue(pharmacist.hasPermission(Permission.PHARMACY_READ));
//         assertTrue(pharmacist.hasPermission(Permission.PHARMACY_UPDATE));
        
//         assertFalse(pharmacist.hasPermission(Permission.PATIENT_READ));
//         assertFalse(pharmacist.hasPermission(Permission.RECORD_READ));
//         assertFalse(pharmacist.hasPermission(Permission.APPOINTMENT_READ));
//         assertFalse(pharmacist.hasPermission(Permission.INVOICE_READ));
//         assertFalse(pharmacist.hasPermission(Permission.USER_READ));
//     }

//     @Test
//     @DisplayName("Role should support hasAnyPermission and hasAllPermissions")
//     void roleSupportsPermissionChecks() {
//         Role admin = Role.admin();
//         assertTrue(admin.hasAnyPermission(Permission.USER_CREATE, Permission.PATIENT_CREATE));
//         assertTrue(admin.hasAllPermissions(Permission.USER_CREATE, Permission.PATIENT_CREATE, Permission.RECORD_CREATE));

//         Role doctor = Role.doctor();
//         assertTrue(doctor.hasAnyPermission(Permission.USER_CREATE, Permission.PATIENT_CREATE));
//         assertFalse(doctor.hasAllPermissions(Permission.USER_CREATE, Permission.PATIENT_CREATE));
//     }

//     @Test
//     @DisplayName("Add and remove permission should work")
//     void addRemovePermission() {
//         Role role = Role.builder()
//                 .name("TEST_ROLE")
//                 .description("Test role")
//                 .permissions(Set.of(Permission.PATIENT_READ))
//                 .build();

//         assertTrue(role.hasPermission(Permission.PATIENT_READ));
//         assertFalse(role.hasPermission(Permission.PATIENT_CREATE));

//         role.addPermission(Permission.PATIENT_CREATE);
//         assertTrue(role.hasPermission(Permission.PATIENT_CREATE));

//         role.removePermission(Permission.PATIENT_READ);
//         assertFalse(role.hasPermission(Permission.PATIENT_READ));
//     }
// }
