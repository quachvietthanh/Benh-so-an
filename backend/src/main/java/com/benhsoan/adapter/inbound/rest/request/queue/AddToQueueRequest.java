package com.benhsoan.adapter.inbound.rest.request.queue;

import java.util.UUID;

import com.benhsoan.domain.queue.enums.PriorityLevel;

import jakarta.validation.constraints.NotNull;

public record AddToQueueRequest(

        @NotNull(message = "Mã bệnh nhân không được để trống")
        UUID patientId,

        @NotNull(message = "Mức ưu tiên không được để trống")
        PriorityLevel priorityLevel,

        @NotNull(message = "Phòng khám không được để trống")
        String roomNumber,

        UUID doctorId

) {
}
