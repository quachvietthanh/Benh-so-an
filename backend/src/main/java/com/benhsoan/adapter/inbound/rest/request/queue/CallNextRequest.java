package com.benhsoan.adapter.inbound.rest.request.queue;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CallNextRequest(

        @NotNull(message = "Mã bác sĩ không được để trống")
        UUID doctorId,

        String roomNumber

) {
}
