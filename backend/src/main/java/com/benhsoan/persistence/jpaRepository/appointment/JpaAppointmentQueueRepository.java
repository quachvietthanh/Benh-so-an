package com.benhsoan.persistence.jpaRepository.appointment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benhsoan.domain.appointment.enums.QueueStatus;
import com.benhsoan.persistence.entity.appointment.AppointmentQueueEntity;

public interface JpaAppointmentQueueRepository extends JpaRepository<AppointmentQueueEntity, UUID> {

    Optional<AppointmentQueueEntity> findByAppointmentId(UUID appointmentId);

    List<AppointmentQueueEntity> findByStatus(QueueStatus status);

}