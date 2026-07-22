package com.benhsoan.port.outbound.repository.crudRepository.appointment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import com.benhsoan.domain.appointment.Appointment;
import com.benhsoan.persistence.entity.appointment.AppointmentEntity;
import com.benhsoan.port.dto.command.appointment.SearchAppointmentCommand;
import com.benhsoan.port.outbound.repository.BaseRepository;

public interface AppointmentRepository
        extends BaseRepository<Appointment, UUID> {

    Optional<Appointment> findByAppointmentCode(String appointmentCode);

    boolean existsByAppointmentCode(String appointmentCode);

    Page<Appointment> search(SearchAppointmentCommand command);

    List<Appointment> findAll( Specification<AppointmentEntity> specification);

    Page<Appointment> findAll(
            Specification<AppointmentEntity> specification,
            org.springframework.data.domain.Pageable pageable
    );

    boolean exists( Specification<AppointmentEntity> specification);

    long count( Specification<AppointmentEntity> specification);

}