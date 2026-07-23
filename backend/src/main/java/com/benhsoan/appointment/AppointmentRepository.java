package com.benhsoan.appointment;

import java.time.Instant;
import java.time.LocalDate;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {
    interface PublicLookupProjection {
        Timestamp getAppointmentAt();
        String getStatus();
    }

    List<AppointmentEntity> findAllByOrderByAppointmentAtDesc();
    List<AppointmentEntity> findByStatusOrderByCheckedInAtAsc(AppointmentStatus status);
    boolean existsByDoctorIdAndAppointmentAtBetweenAndStatusNot(
            UUID doctorId, Instant from, Instant to, AppointmentStatus excludedStatus);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<AppointmentEntity> findByStatusAndReminderSentAtIsNullAndAppointmentAtBetween(
            AppointmentStatus status, Instant from, Instant to);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT appointment FROM AppointmentEntity appointment WHERE appointment.id = :id")
    Optional<AppointmentEntity> findByIdForStatusChange(@Param("id") UUID id);

    @Query(value = """
            SELECT a.appointment_at AS appointmentAt, a.status AS status
            FROM appointments a
            INNER JOIN patients p ON p.id = a.patient_id
            WHERE a.appointment_code = :appointmentCode
              AND p.date_of_birth = :dateOfBirth
            LIMIT 1
            """, nativeQuery = true)
    Optional<PublicLookupProjection> findPublicLookup(
            @Param("appointmentCode") String appointmentCode,
            @Param("dateOfBirth") LocalDate dateOfBirth);
}
