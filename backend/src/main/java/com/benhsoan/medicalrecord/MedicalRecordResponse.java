package com.benhsoan.medicalrecord;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record MedicalRecordResponse(
        UUID id, String recordCode, UUID patientId, String patientName,
        UUID doctorId, String doctorName, String symptoms, String examinationNote,
        String diagnosis, String treatmentPlan, List<String> clinicalOrders,
        Map<String, String> clinicalResults, String status, Instant createdAt,
        List<AttachmentInfo> attachments
) {
    public record AttachmentInfo(UUID id, String fileName, String contentType, long fileSize) {}
}
