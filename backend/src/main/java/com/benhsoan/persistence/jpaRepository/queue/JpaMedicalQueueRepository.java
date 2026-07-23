package com.benhsoan.persistence.jpaRepository.queue;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.benhsoan.domain.queue.enums.QueueStatus;
import com.benhsoan.persistence.entity.queue.MedicalQueueEntity;

public interface JpaMedicalQueueRepository
        extends JpaRepository<MedicalQueueEntity, UUID> {

    @Query("""
            SELECT q FROM MedicalQueueEntity q
            WHERE q.roomNumber = :roomNumber
              AND q.status = :status
            ORDER BY
                CASE q.priorityLevel
                    WHEN 'EMERGENCY' THEN 0
                    WHEN 'APPOINTMENT' THEN 1
                    ELSE 2
                END,
                q.queueNumber ASC
            """)
    List<MedicalQueueEntity> findByRoomNumberAndStatusOrderByQueueNumberAsc(
            @Param("roomNumber") String roomNumber,
            @Param("status") QueueStatus status
    );

    @Query("""
            SELECT q FROM MedicalQueueEntity q
            WHERE q.doctorId = :doctorId
              AND q.status = :status
            ORDER BY
                CASE q.priorityLevel
                    WHEN 'EMERGENCY' THEN 0
                    WHEN 'APPOINTMENT' THEN 1
                    ELSE 2
                END,
                q.queueNumber ASC
            """)
    List<MedicalQueueEntity> findByDoctorIdAndStatusOrderByQueueNumberAsc(
            @Param("doctorId") UUID doctorId,
            @Param("status") QueueStatus status
    );

    @Query("""
            SELECT q FROM MedicalQueueEntity q
            WHERE q.roomNumber = :roomNumber
              AND q.status IN :statuses
            ORDER BY
                CASE q.priorityLevel
                    WHEN 'EMERGENCY' THEN 0
                    WHEN 'APPOINTMENT' THEN 1
                    ELSE 2
                END,
                q.queueNumber ASC
            """)
    List<MedicalQueueEntity> findByRoomNumberAndStatusIn(
            @Param("roomNumber") String roomNumber,
            @Param("statuses") List<QueueStatus> statuses
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT q FROM MedicalQueueEntity q
            WHERE q.roomNumber = :roomNumber
              AND q.status = 'WAITING'
            ORDER BY
                CASE q.priorityLevel
                    WHEN 'EMERGENCY' THEN 0
                    WHEN 'APPOINTMENT' THEN 1
                    ELSE 2
                END,
                q.queueNumber ASC
            LIMIT 1
            """)
    Optional<MedicalQueueEntity> findNextWaiting(
            @Param("roomNumber") String roomNumber
    );

    int countByRoomNumberAndStatus(
            String roomNumber,
            QueueStatus status
    );

    int countByDoctorIdAndStatus(
            UUID doctorId,
            QueueStatus status
    );

    @Query("""
            SELECT COALESCE(MAX(q.queueNumber), 0)
            FROM MedicalQueueEntity q
            WHERE q.roomNumber = :roomNumber
              AND q.createdAt BETWEEN :startOfDay AND :endOfDay
            """)
    int findMaxQueueNumberForToday(
            @Param("roomNumber") String roomNumber,
            @Param("startOfDay") Instant startOfDay,
            @Param("endOfDay") Instant endOfDay
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
