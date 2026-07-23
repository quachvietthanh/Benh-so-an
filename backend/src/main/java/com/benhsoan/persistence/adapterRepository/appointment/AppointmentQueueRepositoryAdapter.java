package com.benhsoan.persistence.adapterRepository.appointment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.benhsoan.domain.appointment.AppointmentQueue;
import com.benhsoan.domain.appointment.enums.AppointmentQueueStatus;
import com.benhsoan.persistence.entity.appointment.AppointmentQueueEntity;
import com.benhsoan.persistence.jpaRepository.appointment.JpaAppointmentQueueRepository;
import com.benhsoan.persistence.mapper.appointment.AppointmentQueuePersistenceMapper;
import com.benhsoan.port.outbound.repository.crudRepository.appointment.AppointmentQueueRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AppointmentQueueRepositoryAdapter
        implements AppointmentQueueRepository {

    private final JpaAppointmentQueueRepository jpaRepository;

    private final AppointmentQueuePersistenceMapper mapper;

    @Override
    public Optional<AppointmentQueue> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<AppointmentQueue> findByAppointmentId(UUID appointmentId) {
        return jpaRepository.findByAppointmentId(appointmentId)
                .map(mapper::toDomain);
    }

    @Override
    public AppointmentQueue save(AppointmentQueue queue) {

        AppointmentQueueEntity entity = mapper.toEntity(queue);

        AppointmentQueueEntity saved = jpaRepository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public List<AppointmentQueue> findByStatusOrderByQueueNumberAsc(
            AppointmentQueueStatus status) {
        return jpaRepository.findByStatusOrderByQueueNumberAsc(status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<AppointmentQueue> findAllByOrderByQueueNumberAsc() {
        return jpaRepository.findAllByOrderByQueueNumberAsc()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            return;
        }
        jpaRepository.deleteById(id);
    }

}
