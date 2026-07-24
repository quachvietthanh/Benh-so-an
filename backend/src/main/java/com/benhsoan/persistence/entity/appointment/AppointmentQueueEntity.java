package com.benhsoan.persistence.entity.appointment;

import java.time.Instant;
import java.util.UUID;

import com.benhsoan.domain.appointment.enums.AppointmentQueueStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "appointment_queues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentQueueEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "appointment_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID appointmentId;

    @Column(name = "queue_number", nullable = false)
    private Integer queueNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AppointmentQueueStatus status;

    @Column(name = "checked_in_at", nullable = false)
    private Instant checkedInAt;

    @Column(name = "called_at")
    private Instant calledAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}
