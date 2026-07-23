package com.benhsoan.application.ucservice.queue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.benhsoan.domain.queue.MedicalQueue;
import com.benhsoan.domain.queue.enums.PriorityLevel;
import com.benhsoan.domain.queue.enums.QueueStatus;
import com.benhsoan.domain.queue.exception.QueueNotFoundException;
import com.benhsoan.port.dto.command.queue.CallNextCommand;
import com.benhsoan.port.dto.result.QueueResult;
import com.benhsoan.port.outbound.repository.crudRepository.queue.MedicalQueueRepository;

@DisplayName("CallNextService Tests")
@ExtendWith(MockitoExtension.class)
class CallNextServiceTest {

    @Mock
    private MedicalQueueRepository medicalQueueRepository;

    @InjectMocks
    private CallNextService service;

    private final UUID doctorId = UUID.randomUUID();
    private final UUID patientId = UUID.randomUUID();

    @Test
    @DisplayName("Should call next waiting patient")
    void callNextSuccess() {
        MedicalQueue waiting = MedicalQueue.create(
                patientId, 1, PriorityLevel.REGULAR, "Room 101", UUID.randomUUID()
        );

        when(medicalQueueRepository.findNextWaiting("Room 101"))
                .thenReturn(Optional.of(waiting));

        when(medicalQueueRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CallNextCommand command = new CallNextCommand(doctorId, "Room 101");
        QueueResult result = service.callNext(command);

        assertEquals(QueueStatus.IN_PROGRESS, result.status());
        assertEquals(doctorId, result.doctorId());
        assertEquals(patientId, result.patientId());
        assertNotNull(result.calledAt());
        assertNotNull(result.startedAt());
    }

    @Test
    @DisplayName("Should throw when no waiting patient")
    void callNextNoPatient() {
        when(medicalQueueRepository.findNextWaiting("Room 101"))
                .thenReturn(Optional.empty());

        CallNextCommand command = new CallNextCommand(doctorId, "Room 101");

        assertThrows(QueueNotFoundException.class,
                () -> service.callNext(command));
    }
}
