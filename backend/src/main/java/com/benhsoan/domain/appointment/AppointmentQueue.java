package com.benhsoan.domain.appointment;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.benhsoan.domain.appointment.enums.AppointmentQueueStatus;
import com.benhsoan.domain.appointment.exception.AppointmentQueueInvalidStatusException;
import com.benhsoan.domain.shared.Guard.Guard;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppointmentQueue {

    private UUID id;

    private UUID appointmentId;

    private Integer queueNumber;

    private AppointmentQueueStatus status;

    private Instant checkedInAt;

    private Instant calledAt;

    private Instant startedAt;

    private Instant completedAt;

    private Instant createdAt;

    private AppointmentQueue(
            UUID id,
            UUID appointmentId,
            Integer queueNumber,
            AppointmentQueueStatus status,
            Instant checkedInAt,
            Instant calledAt,
            Instant startedAt,
            Instant completedAt,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.appointmentId = Objects.requireNonNull(appointmentId);
        this.queueNumber = Guard.require(queueNumber, "Queue number");
        this.status = Guard.require(status, "Queue status");
        this.checkedInAt = checkedInAt;
        this.calledAt = calledAt;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static AppointmentQueue create(
            UUID appointmentId,
            Integer queueNumber,
            Instant checkedInAt
    ) {
        return new AppointmentQueue(
                UUID.randomUUID(),
                appointmentId,
                queueNumber,
                AppointmentQueueStatus.WAITING,
                Guard.require(checkedInAt, "Checked in time"),
                null,
                null,
                null,
                Instant.now()
        );
    }   
     public static AppointmentQueue restore(
            UUID id,
            UUID appointmentId,
            Integer queueNumber,
            AppointmentQueueStatus status,
            Instant checkedInAt,
            Instant calledAt,
            Instant startedAt,
            Instant completedAt,
            Instant createdAt
    ) {
        return new AppointmentQueue(
                id,
                appointmentId,
                queueNumber,
                status,
                checkedInAt,
                calledAt,
                startedAt,
                completedAt,
                createdAt
        );
    }

    public boolean canCall() {
        return status == AppointmentQueueStatus.WAITING;
    }

    public boolean canStart() {
        return status == AppointmentQueueStatus.CALLING;
    }

    public boolean canComplete() {
        return status == AppointmentQueueStatus.IN_PROGRESS;
    }

    public boolean canSkip() {
        return status == AppointmentQueueStatus.WAITING
                || status == AppointmentQueueStatus.CALLING;
    }

    public void call(Instant calledAt) {
        if (!canCall()) {
            throw new AppointmentQueueInvalidStatusException(
                    "Only waiting patients can be called."
            );
        }

        this.calledAt = Guard.require(calledAt, "Called time");
        this.status = AppointmentQueueStatus.CALLING;
    }

    public void start(Instant startedAt) {
        if (!canStart()) {
            throw new AppointmentQueueInvalidStatusException(
                    "Only called patients can start examination."
            );
        }

        this.startedAt = Guard.require(startedAt, "Started time");
        this.status = AppointmentQueueStatus.IN_PROGRESS;
    }

    public void complete(Instant completedAt) {
        if (!canComplete()) {
            throw new AppointmentQueueInvalidStatusException(
                    "Only patients in progress can complete examination."
            );
        }

        this.completedAt = Guard.require(completedAt, "Completed time");
        this.status = AppointmentQueueStatus.COMPLETED;
    }

    public void skip() {
        if (!canSkip()) {
            throw new AppointmentQueueInvalidStatusException(
                    "Queue cannot be skipped."
            );
        }

        this.status = AppointmentQueueStatus.SKIPPED;
    }
}
