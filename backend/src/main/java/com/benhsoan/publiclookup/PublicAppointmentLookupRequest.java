package com.benhsoan.publiclookup;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PublicAppointmentLookupRequest(
        @NotBlank(message = "Vui lòng nhập mã lịch hẹn")
        @Size(max = 20, message = "Mã lịch hẹn không được vượt quá 20 ký tự")
        @Pattern(
                regexp = "^[A-Za-z0-9-]{4,20}$",
                message = "Mã lịch hẹn không đúng định dạng")
        String appointmentCode,

        @NotNull(message = "Vui lòng nhập ngày sinh")
        @PastOrPresent(message = "Ngày sinh không hợp lệ")
        LocalDate dateOfBirth
) {
}
