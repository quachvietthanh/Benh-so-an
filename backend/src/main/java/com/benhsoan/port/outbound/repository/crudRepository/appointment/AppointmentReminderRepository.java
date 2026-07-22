package com.benhsoan.port.outbound.repository.crudRepository.appointment;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.benhsoan.domain.appointment.AppointmentReminder;
import com.benhsoan.domain.appointment.enums.ReminderStatus;
import com.benhsoan.port.outbound.repository.BaseRepository;

public interface AppointmentReminderRepository
        extends BaseRepository<AppointmentReminder, UUID> {

    Optional<AppointmentReminder> findByAppointmentId(UUID appointmentId);

    List<AppointmentReminder> findByStatus(ReminderStatus status);

    List<AppointmentReminder> findByStatusAndRemindAtLessThanEqual(
            ReminderStatus status,
            Instant remindAt
    );

}