package com.benhsoan.domain.appointment;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.benhsoan.domain.appointment.enums.ReminderChannel;
import com.benhsoan.domain.appointment.enums.ReminderStatus;
import com.benhsoan.domain.appointment.exception.AppointmentReminderAlreadySentException;
import com.benhsoan.domain.appointment.exception.AppointmentReminderInvalidStatusException;
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
public class AppointmentReminder {

    private UUID id;

    private UUID appointmentId;

    private ReminderChannel channel;

    private Instant remindAt;

    private ReminderStatus status;

    private Instant sentAt;

    private Instant createdAt;

    private AppointmentReminder(
            UUID id,
            UUID appointmentId,
            ReminderChannel channel,
            Instant remindAt,
            ReminderStatus status,
            Instant sentAt,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.appointmentId = Objects.requireNonNull(appointmentId);
        this.channel = Guard.require(channel, "Reminder channel");
        this.remindAt = Guard.require(remindAt, "Reminder time");
        this.status = Guard.require(status, "Reminder status");
        this.sentAt = sentAt;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static AppointmentReminder create(
            UUID appointmentId,
            ReminderChannel channel,
            Instant remindAt
    ) {
        return new AppointmentReminder(
                UUID.randomUUID(),
                appointmentId,
                channel,
                remindAt,
                ReminderStatus.PENDING,
                null,
                Instant.now()
        );
    }    
    public static AppointmentReminder restore(
            UUID id,
            UUID appointmentId,
            ReminderChannel channel,
            Instant remindAt,
            ReminderStatus status,
            Instant sentAt,
            Instant createdAt
    ) {
        return new AppointmentReminder(
                id,
                appointmentId,
                channel,
                remindAt,
                status,
                sentAt,
                createdAt
        );
    }

    public boolean canSend() {
        return status == ReminderStatus.PENDING;
    }

    public boolean canFail() {
        return status == ReminderStatus.PENDING;
    }

    public boolean canCancel() {
        return status == ReminderStatus.PENDING;
    }
    
    public void markSent(Instant sentAt) {
    if (status == ReminderStatus.SENT) {
        throw new AppointmentReminderAlreadySentException();
    }

    if (!canSend()) {
        throw new AppointmentReminderInvalidStatusException(
                "Only pending reminders can be marked as sent."
        );
    }

    this.sentAt = Guard.require(sentAt, "Sent time");
    this.status = ReminderStatus.SENT;
}

    public void markFailed() {
        if (!canFail()) {
            throw new AppointmentReminderInvalidStatusException(
                    "Only pending reminders can be marked as failed."
            );
        }

        this.status = ReminderStatus.FAILED;
    }

    public void cancel() {
        if (!canCancel()) {
            throw new AppointmentReminderInvalidStatusException(
                    "Only pending reminders can be cancelled."
            );
        }

        this.status = ReminderStatus.CANCELLED;
    }
}