package com.benhsoan.port.outbound.repository.crudRepository.queue;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.benhsoan.domain.queue.MedicalQueue;
import com.benhsoan.domain.queue.enums.QueueStatus;
import com.benhsoan.port.outbound.repository.BaseRepository;

public interface MedicalQueueRepository
        extends BaseRepository<MedicalQueue, UUID> {

    List<MedicalQueue> findByRoomNumberAndStatus(
            String roomNumber,
            QueueStatus status,
            int page,
            int size
    );

    List<MedicalQueue> findByDoctorIdAndStatus(
            UUID doctorId,
            QueueStatus status,
            int page,
            int size
    );

    List<MedicalQueue> findByRoomNumberAndStatusIn(
            String roomNumber,
            List<QueueStatus> statuses
    );

    Optional<MedicalQueue> findNextWaiting(
            String roomNumber
    );

    int countByRoomNumberAndStatus(
            String roomNumber,
            QueueStatus status
    );

    int countByDoctorIdAndStatus(
            UUID doctorId,
            QueueStatus status
    );

    int findMaxQueueNumberForToday(
            String roomNumber,
            Instant startOfDay,
            Instant endOfDay
    );

    long countByRoomNumberAndStatusIn(
            String roomNumber,
            List<QueueStatus> statuses
    );

    long countByDoctorIdAndStatusIn(
            UUID doctorId,
            List<QueueStatus> statuses
    );
}
