package com.benhsoan.application.ucservice.queue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.benhsoan.domain.queue.MedicalQueue;
import com.benhsoan.domain.queue.enums.PriorityLevel;
import com.benhsoan.domain.queue.enums.QueueStatus;
import com.benhsoan.domain.queue.exception.InvalidStatusTransitionException;
import com.benhsoan.domain.queue.exception.QueueNotFoundException;
import com.benhsoan.port.dto.command.queue.UpdateQueueStatusCommand;
import com.benhsoan.port.dto.result.QueueResult;
import com.benhsoan.port.outbound.repository.crudRepository.queue.MedicalQueueRepository;

@DisplayName("UpdateQueueStatusService Tests")
@ExtendWith(MockitoExtension.class)
class UpdateQueueStatusServiceTest {

    @Mock
    private MedicalQueueRepository medicalQueueRepository;

    @InjectMocks
    private UpdateQueueStatusService service;

    private final UUID queueId = UUID.randomUUID();
    private final UUID doctorId = UUID.randomUUID();

    private MedicalQueue createQueueWithStatus(QueueStatus status) {
        MedicalQueue q = MedicalQueue.create(
                UUID.randomUUID(), 1, PriorityLevel.REGULAR,
                "Room 101", UUID.randomUUID()
        );
        if (status == QueueStatus.IN_PROGRESS) q.call(doctorId);
        if (status == QueueStatus.WAITING_FOR_RESULT) {
            q.call(doctorId);
            q.sendToWaitingForResult();
        }
        if (status == QueueStatus.SKIPPED) {
            q.skip();
        }
        if (status == QueueStatus.COMPLETED) {
            q.call(doctorId);
            q.complete();
        }
        if (status == QueueStatus.CANCELLED) {
            q.cancel("Cancelled");
        }
        return q;
    }

    @Nested
    @DisplayName("Valid transitions")
    class ValidTransitions {

        @Test
        @DisplayName("WAITING → IN_PROGRESS")
        void waitingToInProgress() {
            MedicalQueue queue = createQueueWithStatus(QueueStatus.WAITING);
            when(medicalQueueRepository.findById(queueId))
                    .thenReturn(Optional.of(queue));
            when(medicalQueueRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UpdateQueueStatusCommand cmd = new UpdateQueueStatusCommand(
                    queueId, QueueStatus.IN_PROGRESS, doctorId, null
            );
            QueueResult result = service.updateStatus(cmd);

            assertEquals(QueueStatus.IN_PROGRESS, result.status());
        }

        @Test
        @DisplayName("WAITING → SKIPPED")
        void waitingToSkipped() {
            MedicalQueue queue = createQueueWithStatus(QueueStatus.WAITING);
            when(medicalQueueRepository.findById(queueId))
                    .thenReturn(Optional.of(queue));
            when(medicalQueueRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UpdateQueueStatusCommand cmd = new UpdateQueueStatusCommand(
                    queueId, QueueStatus.SKIPPED, null, null
            );
            QueueResult result = service.updateStatus(cmd);

            assertEquals(QueueStatus.SKIPPED, result.status());
        }

        @Test
        @DisplayName("SKIPPED → IN_PROGRESS (resume)")
        void skippedToInProgress() {
            MedicalQueue queue = createQueueWithStatus(QueueStatus.SKIPPED);
            when(medicalQueueRepository.findById(queueId))
                    .thenReturn(Optional.of(queue));
            when(medicalQueueRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UpdateQueueStatusCommand cmd = new UpdateQueueStatusCommand(
                    queueId, QueueStatus.IN_PROGRESS, doctorId, null
            );
            QueueResult result = service.updateStatus(cmd);

            assertEquals(QueueStatus.IN_PROGRESS, result.status());
        }

        @Test
        @DisplayName("WAITING → CANCELLED")
        void waitingToCancelled() {
            MedicalQueue queue = createQueueWithStatus(QueueStatus.WAITING);
            when(medicalQueueRepository.findById(queueId))
                    .thenReturn(Optional.of(queue));
            when(medicalQueueRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UpdateQueueStatusCommand cmd = new UpdateQueueStatusCommand(
                    queueId, QueueStatus.CANCELLED, null, "Patient no-show"
            );
            QueueResult result = service.updateStatus(cmd);

            assertEquals(QueueStatus.CANCELLED, result.status());
            assertEquals("Patient no-show", result.cancelReason());
        }

        @Test
        @DisplayName("IN_PROGRESS → WAITING_FOR_RESULT")
        void inProgressToWaitingForResult() {
            MedicalQueue queue = createQueueWithStatus(QueueStatus.IN_PROGRESS);
            when(medicalQueueRepository.findById(queueId))
                    .thenReturn(Optional.of(queue));
            when(medicalQueueRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UpdateQueueStatusCommand cmd = new UpdateQueueStatusCommand(
                    queueId, QueueStatus.WAITING_FOR_RESULT, null, null
            );
            QueueResult result = service.updateStatus(cmd);

            assertEquals(QueueStatus.WAITING_FOR_RESULT, result.status());
        }

        @Test
        @DisplayName("IN_PROGRESS → COMPLETED")
        void inProgressToCompleted() {
            MedicalQueue queue = createQueueWithStatus(QueueStatus.IN_PROGRESS);
            when(medicalQueueRepository.findById(queueId))
                    .thenReturn(Optional.of(queue));
            when(medicalQueueRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UpdateQueueStatusCommand cmd = new UpdateQueueStatusCommand(
                    queueId, QueueStatus.COMPLETED, null, null
            );
            QueueResult result = service.updateStatus(cmd);

            assertEquals(QueueStatus.COMPLETED, result.status());
        }

        @Test
        @DisplayName("WAITING_FOR_RESULT → IN_PROGRESS (resume)")
        void waitingForResultToInProgress() {
            MedicalQueue queue = createQueueWithStatus(QueueStatus.WAITING_FOR_RESULT);
            when(medicalQueueRepository.findById(queueId))
                    .thenReturn(Optional.of(queue));
            when(medicalQueueRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UpdateQueueStatusCommand cmd = new UpdateQueueStatusCommand(
                    queueId, QueueStatus.IN_PROGRESS, doctorId, null
            );
            QueueResult result = service.updateStatus(cmd);

            assertEquals(QueueStatus.IN_PROGRESS, result.status());
        }

        @Test
        @DisplayName("WAITING_FOR_RESULT → COMPLETED")
        void waitingForResultToCompleted() {
            MedicalQueue queue = createQueueWithStatus(QueueStatus.WAITING_FOR_RESULT);
            when(medicalQueueRepository.findById(queueId))
                    .thenReturn(Optional.of(queue));
            when(medicalQueueRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UpdateQueueStatusCommand cmd = new UpdateQueueStatusCommand(
                    queueId, QueueStatus.COMPLETED, null, null
            );
            QueueResult result = service.updateStatus(cmd);

            assertEquals(QueueStatus.COMPLETED, result.status());
        }

        @Test
        @DisplayName("WAITING_FOR_RESULT → CANCELLED")
        void waitingForResultToCancelled() {
            MedicalQueue queue = createQueueWithStatus(QueueStatus.WAITING_FOR_RESULT);
            when(medicalQueueRepository.findById(queueId))
                    .thenReturn(Optional.of(queue));
            when(medicalQueueRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UpdateQueueStatusCommand cmd = new UpdateQueueStatusCommand(
                    queueId, QueueStatus.CANCELLED, null, "Lab closed"
            );
            QueueResult result = service.updateStatus(cmd);

            assertEquals(QueueStatus.CANCELLED, result.status());
        }
    }

    @Nested
    @DisplayName("Invalid transitions")
    class InvalidTransitions {

        @Test
        @DisplayName("Should throw when queue not found")
        void queueNotFound() {
            when(medicalQueueRepository.findById(queueId))
                    .thenReturn(Optional.empty());

            UpdateQueueStatusCommand cmd = new UpdateQueueStatusCommand(
                    queueId, QueueStatus.IN_PROGRESS, doctorId, null
            );

            assertThrows(QueueNotFoundException.class,
                    () -> service.updateStatus(cmd));
        }

        @Test
        @DisplayName("WAITING → COMPLETED should throw")
        void waitingToCompletedInvalid() {
            MedicalQueue queue = createQueueWithStatus(QueueStatus.WAITING);
            when(medicalQueueRepository.findById(queueId))
                    .thenReturn(Optional.of(queue));

            UpdateQueueStatusCommand cmd = new UpdateQueueStatusCommand(
                    queueId, QueueStatus.COMPLETED, null, null
            );

            assertThrows(InvalidStatusTransitionException.class,
                    () -> service.updateStatus(cmd));
        }

        @Test
        @DisplayName("COMPLETED → CANCELLED should throw")
        void completedToCancelledInvalid() {
            MedicalQueue queue = createQueueWithStatus(QueueStatus.COMPLETED);
            when(medicalQueueRepository.findById(queueId))
                    .thenReturn(Optional.of(queue));

            UpdateQueueStatusCommand cmd = new UpdateQueueStatusCommand(
                    queueId, QueueStatus.CANCELLED, null, "Late cancel"
            );

            assertThrows(InvalidStatusTransitionException.class,
                    () -> service.updateStatus(cmd));
        }

        @Test
        @DisplayName("CANCELLED → IN_PROGRESS should throw")
        void cancelledToInProgressInvalid() {
            MedicalQueue queue = createQueueWithStatus(QueueStatus.CANCELLED);
            when(medicalQueueRepository.findById(queueId))
                    .thenReturn(Optional.of(queue));

            UpdateQueueStatusCommand cmd = new UpdateQueueStatusCommand(
                    queueId, QueueStatus.IN_PROGRESS, doctorId, null
            );

            assertThrows(InvalidStatusTransitionException.class,
                    () -> service.updateStatus(cmd));
        }
    }
}
