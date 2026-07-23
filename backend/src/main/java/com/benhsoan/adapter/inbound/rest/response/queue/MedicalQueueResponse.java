package com.benhsoan.adapter.inbound.rest.response.queue;

import java.time.Instant;
import java.util.UUID;

import com.benhsoan.domain.queue.enums.PriorityLevel;
import com.benhsoan.domain.queue.enums.QueueStatus;

public record MedicalQueueResponse(

        UUID id,
        UUID patientId,
        String patientName,
        UUID doctorId,
        String doctorName,
        String roomNumber,
        int queueNumber,
        QueueStatus status,
        PriorityLevel priorityLevel,
        String notes,
        Instant checkedInAt,
        Instant calledAt,
        Instant startedAt,
        Instant waitingForResultAt,
        Instant completedAt,
        Instant cancelledAt,
        String cancelReason,
        Instant createdAt,
        Instant updatedAt

) {
}
