package com.benhsoan.appointment;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appointments")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentEntity {
    @Id @Column(columnDefinition = "BINARY(16)") private UUID id;
    @Column(name = "appointment_code", nullable = false, unique = true) private String appointmentCode;
    @Column(name = "patient_id", nullable = false, columnDefinition = "BINARY(16)") private UUID patientId;
    @Column(name = "doctor_id", nullable = false, columnDefinition = "BINARY(16)") private UUID doctorId;
    @Column(nullable = false) private String department;
    @Column(name = "appointment_at", nullable = false) private Instant appointmentAt;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private AppointmentStatus status;
    private String reason;
    @Column(name = "cancel_reason") private String cancelReason;
    @Column(name = "checked_in_at") private Instant checkedInAt;
    @Column(name = "reminder_sent_at") private Instant reminderSentAt;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
}
