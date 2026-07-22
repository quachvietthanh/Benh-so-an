package com.benhsoan.medicalrecord;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MedicalRecordRequest(
        @NotNull UUID patientId,
        @NotBlank String symptoms,
        String examinationNote,
        @NotBlank String diagnosis,
        String treatmentPlan,
        List<String> clinicalOrders,
        Map<String, String> clinicalResults
) {}
