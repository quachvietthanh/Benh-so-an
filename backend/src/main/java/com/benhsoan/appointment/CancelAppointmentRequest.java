package com.benhsoan.appointment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelAppointmentRequest(
        @NotBlank(message = "Vui lòng nhập lý do hủy")
        @Size(max = 500, message = "Lý do hủy không được vượt quá 500 ký tự")
        String reason
) {
    public CancelAppointmentRequest {
        if (reason != null) {
            reason = reason.trim();
        }
    }
}
