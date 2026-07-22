package com.benhsoan.port.dto.result;

import java.time.Instant;
import java.util.UUID;

import com.benhsoan.domain.appointment.enums.AppointmentStatus;

public record AppointmentResult(

        UUID id,

        String appointmentCode,

        UUID patientId,

        UUID doctorId,

        Instant startTime,

        Instant endTime,

        AppointmentStatus status,

        String reason,

        String cancelReason,

        Instant checkedInAt,

        Instant completedAt,

        UUID createdBy,

        Instant createdAt

) {
}