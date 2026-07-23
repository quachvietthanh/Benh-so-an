package com.benhsoan.port.outbound.repository.crudRepository.appointment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.benhsoan.domain.appointment.AppointmentQueue;
import com.benhsoan.domain.appointment.enums.AppointmentQueueStatus;
import com.benhsoan.port.outbound.repository.BaseRepository;

public interface AppointmentQueueRepository
        extends BaseRepository<AppointmentQueue, UUID> {

    Optional<AppointmentQueue> findByAppointmentId(UUID appointmentId);

    List<AppointmentQueue> findByStatusOrderByQueueNumberAsc(
            AppointmentQueueStatus status
    );

    List<AppointmentQueue> findAllByOrderByQueueNumberAsc();

}
