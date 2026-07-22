package com.benhsoan.appointment;

import java.time.Instant;
import java.util.UUID;

public record AppointmentResponse(
        UUID id, String appointmentCode, UUID patientId, String patientName,
        UUID doctorId, String doctorName, String department, Instant appointmentAt,
        AppointmentStatus status, String reason, String cancelReason,
        Instant checkedInAt, Instant reminderSentAt
) {}
