package com.benhsoan.appointment;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppointmentRequest(
        @NotNull UUID patientId,
        @NotNull UUID doctorId,
        @NotBlank String department,
        @NotNull @Future Instant appointmentAt,
        String reason
) {}
