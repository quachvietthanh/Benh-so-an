package com.benhsoan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDTO {

    private Long id;

    private String patientCode;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @Past(message = "Ngày sinh không hợp lệ")
    private LocalDate dateOfBirth;

    private String gender;

    private String phoneNumber;

    private String email;

    private String address;

    private String identityNumber;

    private String healthInsuranceCode;

    private String bloodType;

    private String emergencyContact;

    private String emergencyPhone;

    private String medicalHistory;

    private String allergies;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
