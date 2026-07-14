package com.benhsoan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordDTO {

    private Long id;

    private String recordCode;

    @NotBlank(message = "Mã bệnh nhân không được để trống")
    private Long patientId;

    private String patientName;

    private String patientCode;

    private Long doctorId;

    private String doctorName;

    private String diagnosis;

    private String symptoms;

    private String treatment;

    private String prescription;

    private String notes;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
