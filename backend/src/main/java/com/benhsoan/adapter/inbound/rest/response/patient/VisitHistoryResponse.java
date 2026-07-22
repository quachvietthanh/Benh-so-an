package com.benhsoan.adapter.inbound.rest.response.patient;

import java.time.Instant;
import java.util.UUID;

import com.benhsoan.domain.patient.Visit;
import com.benhsoan.domain.patient.enums.VisitStatus;
import com.benhsoan.domain.patient.enums.VisitType;

public record VisitHistoryResponse(
        UUID id, String visitCode, UUID doctorId, UUID departmentId,
        VisitType visitType, VisitStatus visitStatus, Instant visitAt,
        String reason, String note
) {
    public static VisitHistoryResponse from(Visit visit) {
        return new VisitHistoryResponse(
                visit.getId(), visit.getVisitCode(), visit.getDoctorId(),
                visit.getDepartmentId(), visit.getVisitType(), visit.getVisitStatus(),
                visit.getVisitAt(), visit.getReason(), visit.getNote());
    }
}
