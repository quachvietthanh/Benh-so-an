package com.benhsoan.application.ucservice.queue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.benhsoan.domain.queue.MedicalQueue;
import com.benhsoan.domain.queue.enums.PriorityLevel;
import com.benhsoan.persistence.entity.appointment.AppointmentEntity;
import com.benhsoan.persistence.jpaRepository.appointment.JpaAppointmentRepository;
import com.benhsoan.port.dto.command.queue.AddToQueueCommand;
import com.benhsoan.port.dto.result.QueueResult;
import com.benhsoan.port.outbound.repository.crudRepository.queue.MedicalQueueRepository;

@DisplayName("AddToQueueService Tests")
@ExtendWith(MockitoExtension.class)
class AddToQueueServiceTest {

    @Mock
    private MedicalQueueRepository medicalQueueRepository;

    @Mock
    private JpaAppointmentRepository jpaAppointmentRepository;

    @InjectMocks
    private AddToQueueService service;

    @Captor
    private ArgumentCaptor<MedicalQueue> queueCaptor;

    private final UUID patientId = UUID.randomUUID();
    private final UUID createdBy = UUID.randomUUID();

    private void mockNoAppointmentToday() {
        when(((JpaSpecificationExecutor<AppointmentEntity>) jpaAppointmentRepository)
                .findOne(any())).thenReturn(Optional.empty());
    }

    private void mockHasAppointmentToday() {
        when(((JpaSpecificationExecutor<AppointmentEntity>) jpaAppointmentRepository)
                .findOne(any())).thenReturn(Optional.of(new AppointmentEntity()));
    }

    @Test
    @DisplayName("Should create queue with next number")
    void addToQueueSuccess() {
        when(medicalQueueRepository.findMaxQueueNumberForToday(
                any(), any(), any())).thenReturn(5);
        mockNoAppointmentToday();

        when(medicalQueueRepository.save(any())).thenAnswer(invocation -> {
            MedicalQueue q = invocation.getArgument(0);
            return q;
        });

        AddToQueueCommand command = new AddToQueueCommand(
                patientId, PriorityLevel.REGULAR, "Room 101", null, createdBy
        );

        QueueResult result = service.addToQueue(command);

        assertNotNull(result);
        assertEquals(patientId, result.patientId());
        assertEquals(6, result.queueNumber());
        assertEquals(PriorityLevel.REGULAR, result.priorityLevel());
        assertEquals("Room 101", result.roomNumber());
        assertEquals(createdBy, result.createdBy());

        verify(medicalQueueRepository).save(queueCaptor.capture());
        MedicalQueue saved = queueCaptor.getValue();
        assertEquals(patientId, saved.getPatientId());
        assertEquals(6, saved.getQueueNumber());
    }

    @Test
    @DisplayName("Should start from number 1 when no queue today")
    void addToQueueFirstOfDay() {
        when(medicalQueueRepository.findMaxQueueNumberForToday(
                any(), any(), any())).thenReturn(0);

        when(medicalQueueRepository.save(any())).thenAnswer(invocation -> {
            MedicalQueue q = invocation.getArgument(0);
            return q;
        });

        AddToQueueCommand command = new AddToQueueCommand(
                patientId, PriorityLevel.EMERGENCY, "Room 102", null, createdBy
        );

        QueueResult result = service.addToQueue(command);

        assertEquals(1, result.queueNumber());
        assertEquals(PriorityLevel.EMERGENCY, result.priorityLevel());
    }

    @Test
    @DisplayName("Should retry on DataIntegrityViolationException")
    void addToQueueRetryOnConflict() {
        when(medicalQueueRepository.findMaxQueueNumberForToday(
                any(), any(), any())).thenReturn(1);
        mockNoAppointmentToday();

        when(medicalQueueRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException("conflict"))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AddToQueueCommand command = new AddToQueueCommand(
                patientId, PriorityLevel.REGULAR, "Room 101", null, createdBy
        );

        QueueResult result = service.addToQueue(command);

        assertNotNull(result);
        assertEquals(2, result.queueNumber());
        verify(medicalQueueRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Should auto-detect APPOINTMENT priority when patient has appointment today")
    void addToQueueWithAppointment() {
        when(medicalQueueRepository.findMaxQueueNumberForToday(
                any(), any(), any())).thenReturn(3);
        mockHasAppointmentToday();

        when(medicalQueueRepository.save(any())).thenAnswer(invocation -> {
            MedicalQueue q = invocation.getArgument(0);
            return q;
        });

        AddToQueueCommand command = new AddToQueueCommand(
                patientId, PriorityLevel.REGULAR, "Room 101", null, createdBy
        );

        QueueResult result = service.addToQueue(command);

        assertEquals(PriorityLevel.APPOINTMENT, result.priorityLevel());
        assertEquals(4, result.queueNumber());
    }

    @Test
    @DisplayName("Should keep EMERGENCY priority even if patient has appointment")
    void addToQueueEmergencyKeepsPriority() {
        when(medicalQueueRepository.findMaxQueueNumberForToday(
                any(), any(), any())).thenReturn(3);

        when(medicalQueueRepository.save(any())).thenAnswer(invocation -> {
            MedicalQueue q = invocation.getArgument(0);
            return q;
        });

        AddToQueueCommand command = new AddToQueueCommand(
                patientId, PriorityLevel.EMERGENCY, "Room 101", null, createdBy
        );

        QueueResult result = service.addToQueue(command);

        assertEquals(PriorityLevel.EMERGENCY, result.priorityLevel());
    }
}
