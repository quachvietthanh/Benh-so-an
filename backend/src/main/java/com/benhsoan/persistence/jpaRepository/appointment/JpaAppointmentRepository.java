package com.benhsoan.persistence.jpaRepository.appointment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.benhsoan.persistence.entity.appointment.AppointmentEntity;

public interface JpaAppointmentRepository
        extends JpaRepository<AppointmentEntity, UUID>,
                JpaSpecificationExecutor<AppointmentEntity> {

    Optional<AppointmentEntity> findByAppointmentCode(String appointmentCode);

    boolean existsByAppointmentCode(String appointmentCode);

    List<AppointmentEntity> findByDoctorId(UUID doctorId);

    List<AppointmentEntity> findByPatientId(UUID patientId);

}