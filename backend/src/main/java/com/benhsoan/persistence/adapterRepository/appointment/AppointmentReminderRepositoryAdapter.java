package com.benhsoan.persistence.adapterRepository.appointment;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.benhsoan.domain.appointment.AppointmentReminder;
import com.benhsoan.domain.appointment.enums.ReminderStatus;
import com.benhsoan.persistence.entity.appointment.AppointmentReminderEntity;
import com.benhsoan.persistence.jpaRepository.appointment.JpaAppointmentReminderRepository;
import com.benhsoan.persistence.mapper.appointment.AppointmentReminderPersistenceMapper;
import com.benhsoan.port.outbound.repository.crudRepository.appointment.AppointmentReminderRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AppointmentReminderRepositoryAdapter
        implements AppointmentReminderRepository {

    private final JpaAppointmentReminderRepository jpaRepository;

    private final AppointmentReminderPersistenceMapper mapper;

    @Override
    public Optional<AppointmentReminder> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<AppointmentReminder> findByAppointmentId(UUID appointmentId) {
        return jpaRepository.findByAppointmentId(appointmentId)
                .map(mapper::toDomain);
    }

    @Override
    public AppointmentReminder save(AppointmentReminder reminder) {

        AppointmentReminderEntity entity = mapper.toEntity(reminder);

        AppointmentReminderEntity saved = jpaRepository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public List<AppointmentReminder> findByStatus(ReminderStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<AppointmentReminder> findByStatusAndRemindAtLessThanEqual(
            ReminderStatus status,
            Instant remindAt) {
        return jpaRepository.findByStatusAndRemindAtLessThanEqual(status, remindAt)
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