package com.benhsoan.dto.request.patient;

import java.time.LocalDate;

import com.benhsoan.domain.patient.enums.BloodType;
import com.benhsoan.domain.patient.enums.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterPatientRequest(

        @NotBlank
        String fullName,

        @NotNull
        LocalDate dateOfBirth,

        @NotNull
        Gender gender,

        String phone,

        String email,

        String address,

        String identityNumber,

        String insuranceNumber,

        BloodType bloodType,

        String emergencyContact,

        String emergencyPhone

) {
}