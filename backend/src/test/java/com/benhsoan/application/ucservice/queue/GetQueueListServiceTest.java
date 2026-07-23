package com.benhsoan.application.ucservice.queue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
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
import com.benhsoan.port.dto.command.queue.GetQueueListQuery;
import com.benhsoan.port.dto.result.PageResponse;
import com.benhsoan.port.dto.result.QueueResult;
import com.benhsoan.port.outbound.repository.crudRepository.queue.MedicalQueueRepository;

@DisplayName("GetQueueListService Tests")
@ExtendWith(MockitoExtension.class)
class GetQueueListServiceTest {

    @Mock
    private MedicalQueueRepository medicalQueueRepository;

    @InjectMocks
    private GetQueueListService service;

    private final UUID doctorId = UUID.randomUUID();

    @Test
    @DisplayName("Should list queue by room")
    void listByRoom() {
        MedicalQueue q = MedicalQueue.create(
                UUID.randomUUID(), 1, PriorityLevel.REGULAR,
                "Room 101", UUID.randomUUID()
        );

        when(medicalQueueRepository.findByRoomNumberAndStatus(
                "Room 101", QueueStatus.WAITING, 0, 20
        )).thenReturn(List.of(q));

        when(medicalQueueRepository.countByRoomNumberAndStatus(
                "Room 101", QueueStatus.WAITING
        )).thenReturn(1);

        GetQueueListQuery query = GetQueueListQuery.byRoom(
                "Room 101", QueueStatus.WAITING, 0, 20
        );

        PageResponse<QueueResult> page = service.getQueueList(query);
        long count = service.count(query);

        assertEquals(1, page.content().size());
        assertEquals(1, count);
        assertEquals("Room 101", page.content().getFirst().roomNumber());
        assertEquals(QueueStatus.WAITING, page.content().getFirst().status());
        assertEquals(1, page.totalElements());
        assertEquals(1, page.totalPages());
    }

    @Test
    @DisplayName("Should list queue by doctor")
    void listByDoctor() {
        MedicalQueue q = MedicalQueue.create(
                UUID.randomUUID(), 1, PriorityLevel.EMERGENCY,
                "Room 102", UUID.randomUUID()
        );

        when(medicalQueueRepository.findByDoctorIdAndStatus(
                doctorId, QueueStatus.WAITING, 0, 10
        )).thenReturn(List.of(q));

        when(medicalQueueRepository.countByDoctorIdAndStatus(
                doctorId, QueueStatus.WAITING
        )).thenReturn(1);

        GetQueueListQuery query = GetQueueListQuery.byDoctor(
                doctorId, QueueStatus.WAITING, 0, 10
        );

        PageResponse<QueueResult> page = service.getQueueList(query);
        long count = service.count(query);

        assertEquals(1, page.content().size());
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Should return empty page when no filter")
    void noFilterReturnsEmpty() {
        GetQueueListQuery query = new GetQueueListQuery(
                null, null, QueueStatus.WAITING, 0, 20
        );

        PageResponse<QueueResult> page = service.getQueueList(query);

        assertTrue(page.content().isEmpty());
        assertEquals(0, page.totalElements());
    }
}
