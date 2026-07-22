package com.benhsoan.adapter.inbound.rest.response.patient;

import java.time.Instant;
import java.util.UUID;

import com.benhsoan.domain.patient.enums.VisitStatus;
import com.benhsoan.domain.patient.enums.VisitType;

public record MedicalHistoryItemResponse(

        UUID id,

        String visitCode,

        VisitType visitType,

        VisitStatus visitStatus,

        Instant visitAt,

        String reason,

        String note,

        UUID doctorId,

        String doctorName,

        UUID departmentId

) {
}
