package com.benhsoan.persistence.jpaRepository.appointment;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.benhsoan.domain.appointment.enums.AppointmentStatus;
import com.benhsoan.persistence.entity.appointment.AppointmentEntity;

public interface JpaAppointmentRepository
        extends JpaRepository<AppointmentEntity, UUID>,
                JpaSpecificationExecutor<AppointmentEntity> {

    List<AppointmentEntity> findByDoctorId(UUID doctorId);

    List<AppointmentEntity> findByPatientId(UUID patientId);

    List<AppointmentEntity> findByStatus(AppointmentStatus status);

    List<AppointmentEntity> findByStartTimeBetween(
            Instant from,
            Instant to
    );

    boolean existsByAppointmentCode(String appointmentCode);

}