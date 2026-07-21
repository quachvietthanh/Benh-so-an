package com.benhsoan.application.ucservice.patient;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.benhsoan.domain.patient.Patient;
import com.benhsoan.port.dto.result.PatientResult;

@Component
public class PatientResultMapper {

    public PatientResult toResult(Patient patient) {

        return new PatientResult(
                patient.getId(),
                patient.getPatientCode(),
                patient.getFullName(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getAddress(),
                patient.getIdentityNumber(),
                patient.getInsuranceNumber(),
                patient.getBloodType(),
                patient.getEmergencyContact(),
                patient.getEmergencyPhone(),
                patient.isActive(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }

    public Page<PatientResult> toResult(Page<Patient> patients) {
        return patients.map(this::toResult);
    }
}