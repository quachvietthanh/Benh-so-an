package com.benhsoan.adapter.inbound.rest.request.appointment;

import jakarta.validation.constraints.NotBlank;

public record CancelAppointmentRequest(

        @NotBlank(message = "Cancel reason is required.")
        String cancelReason

) {
}