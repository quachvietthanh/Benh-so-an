package com.benhsoan.port.dto.result;

import java.time.Instant;
import java.util.UUID;

import com.benhsoan.domain.queue.MedicalQueue;
import com.benhsoan.domain.queue.enums.PriorityLevel;
import com.benhsoan.domain.queue.enums.QueueStatus;

public record QueueResult(

        UUID id,
        UUID patientId,
        UUID doctorId,
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
        UUID createdBy,
        Instant createdAt,
        Instant updatedAt

) {
    public static QueueResult from(MedicalQueue queue) {
        return new QueueResult(
                queue.getId(),
                queue.getPatientId(),
                queue.getDoctorId(),
                queue.getRoomNumber(),
                queue.getQueueNumber(),
                queue.getStatus(),
                queue.getPriorityLevel(),
                queue.getNotes(),
                queue.getCheckedInAt(),
                queue.getCalledAt(),
                queue.getStartedAt(),
                queue.getWaitingForResultAt(),
                queue.getCompletedAt(),
                queue.getCancelledAt(),
                queue.getCancelReason(),
                queue.getCreatedBy(),
                queue.getCreatedAt(),
                queue.getUpdatedAt()
        );
    }
}
