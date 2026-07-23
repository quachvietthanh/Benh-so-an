package com.benhsoan.port.dto.command.queue;

import java.util.UUID;

import com.benhsoan.domain.queue.enums.QueueStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateQueueStatusCommand(

        @NotNull
        UUID queueId,

        @NotNull
        QueueStatus newStatus,

        UUID doctorId,

        String cancelReason

) {
}
