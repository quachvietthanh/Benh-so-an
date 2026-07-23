package com.benhsoan.adapter.inbound.rest.request.queue;

import java.util.UUID;

import com.benhsoan.domain.queue.enums.QueueStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateQueueStatusRequest(

        @NotNull(message = "Trạng thái mới không được để trống")
        QueueStatus newStatus,

        UUID doctorId,

        String cancelReason

) {
}
