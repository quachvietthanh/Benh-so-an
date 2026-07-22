package com.benhsoan.appointment;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {
    List<AppointmentEntity> findAllByOrderByAppointmentAtDesc();
    List<AppointmentEntity> findByStatusOrderByCheckedInAtAsc(AppointmentStatus status);
    boolean existsByDoctorIdAndAppointmentAtBetweenAndStatusNot(
            UUID doctorId, Instant from, Instant to, AppointmentStatus excludedStatus);
    List<AppointmentEntity> findByStatusAndReminderSentAtIsNullAndAppointmentAtBetween(
            AppointmentStatus status, Instant from, Instant to);
}
