package com.benhsoan.persistence.adapterRepository.queue;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.benhsoan.domain.queue.MedicalQueue;
import com.benhsoan.domain.queue.enums.QueueStatus;
import com.benhsoan.persistence.entity.queue.MedicalQueueEntity;
import com.benhsoan.persistence.jpaRepository.queue.JpaMedicalQueueRepository;
import com.benhsoan.persistence.mapper.queue.MedicalQueuePersistenceMapper;
import com.benhsoan.port.outbound.repository.crudRepository.queue.MedicalQueueRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MedicalQueueRepositoryAdapter
        implements MedicalQueueRepository {

    private final JpaMedicalQueueRepository jpaRepository;

    private final MedicalQueuePersistenceMapper mapper;

    @Override
    public Optional<MedicalQueue> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public MedicalQueue save(MedicalQueue queue) {

        MedicalQueueEntity entity = mapper.toEntity(queue);

        MedicalQueueEntity saved = jpaRepository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<MedicalQueue> findByRoomNumberAndStatus(
            String roomNumber,
            QueueStatus status,
            int page,
            int size
    ) {
        return jpaRepository
                .findByRoomNumberAndStatusOrderByQueueNumberAsc(
                        roomNumber, status)
                .stream()
                .skip((long) page * size)
                .limit(size)
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<MedicalQueue> findByDoctorIdAndStatus(
            UUID doctorId,
            QueueStatus status,
            int page,
            int size
    ) {
        return jpaRepository
                .findByDoctorIdAndStatusOrderByQueueNumberAsc(
                        doctorId, status)
                .stream()
                .skip((long) page * size)
                .limit(size)
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<MedicalQueue> findByRoomNumberAndStatusIn(
            String roomNumber,
            List<QueueStatus> statuses
    ) {
        return jpaRepository
                .findByRoomNumberAndStatusIn(roomNumber, statuses)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<MedicalQueue> findNextWaiting(String roomNumber) {
        return jpaRepository.findNextWaiting(roomNumber)
                .map(mapper::toDomain);
    }

    @Override
    public int countByRoomNumberAndStatus(
            String roomNumber,
            QueueStatus status
    ) {
        return jpaRepository.countByRoomNumberAndStatus(
                roomNumber, status);
    }

    @Override
    public int countByDoctorIdAndStatus(
            UUID doctorId,
            QueueStatus status
    ) {
        return jpaRepository.countByDoctorIdAndStatus(
                doctorId, status);
    }

    @Override
    public int findMaxQueueNumberForToday(
            String roomNumber,
            Instant startOfDay,
            Instant endOfDay
    ) {
        return jpaRepository.findMaxQueueNumberForToday(
                roomNumber, startOfDay, endOfDay);
    }

    @Override
    public long countByRoomNumberAndStatusIn(
            String roomNumber,
            List<QueueStatus> statuses
    ) {
        return jpaRepository.countByRoomNumberAndStatusIn(
                roomNumber, statuses);
    }

    @Override
    public long countByDoctorIdAndStatusIn(
            UUID doctorId,
            List<QueueStatus> statuses
    ) {
        return jpaRepository.countByDoctorIdAndStatusIn(
                doctorId, statuses);
    }
}
