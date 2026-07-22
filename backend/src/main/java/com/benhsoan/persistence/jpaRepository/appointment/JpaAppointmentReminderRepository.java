package com.benhsoan.persistence.jpaRepository.appointment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benhsoan.domain.appointment.enums.ReminderStatus;
import com.benhsoan.persistence.entity.appointment.AppointmentReminderEntity;

public interface JpaAppointmentReminderRepository extends JpaRepository<AppointmentReminderEntity, UUID> {

    Optional<AppointmentReminderEntity> findByAppointmentId(UUID appointmentId);

    List<AppointmentReminderEntity> findByStatus(ReminderStatus status);

}