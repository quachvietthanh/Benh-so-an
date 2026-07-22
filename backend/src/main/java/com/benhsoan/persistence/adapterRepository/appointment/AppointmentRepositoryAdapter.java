package com.benhsoan.persistence.adapterRepository.appointment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.benhsoan.domain.appointment.Appointment;
import com.benhsoan.persistence.entity.appointment.AppointmentEntity;
import com.benhsoan.persistence.jpaRepository.appointment.AppointmentSearchSpecification;
import com.benhsoan.persistence.jpaRepository.appointment.JpaAppointmentRepository;
import com.benhsoan.persistence.mapper.appointment.AppointmentPersistenceMapper;
import com.benhsoan.port.dto.command.appointment.SearchAppointmentCommand;
import com.benhsoan.port.outbound.repository.crudRepository.appointment.AppointmentRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AppointmentRepositoryAdapter
        implements AppointmentRepository {

    private final JpaAppointmentRepository jpaRepository;

    private final AppointmentPersistenceMapper mapper;

    @Override
    public Optional<Appointment> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Appointment> findByAppointmentCode(
            String appointmentCode
    ) {
        return jpaRepository.findByAppointmentCode(appointmentCode)
                .map(mapper::toDomain);
    }

    @Override
    public Appointment save(Appointment appointment) {

        AppointmentEntity entity = mapper.toEntity(appointment);

        AppointmentEntity savedEntity = jpaRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            return;
        }

        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByAppointmentCode(String appointmentCode) {
        return jpaRepository.existsByAppointmentCode(appointmentCode);
    }

    @Override
    public Page<Appointment> search(
            SearchAppointmentCommand command
    ) {
        return jpaRepository.findAll(
                AppointmentSearchSpecification.build(command),
                command.pageable()
        ).map(mapper::toDomain);
    }

    @Override
    public List<Appointment> findAll(
            Specification<AppointmentEntity> specification
    ) {
        return jpaRepository.findAll(specification)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Page<Appointment> findAll(
            Specification<AppointmentEntity> specification,
            Pageable pageable
    ) {
        return jpaRepository.findAll(specification, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public boolean exists(
            Specification<AppointmentEntity> specification
    ) {
        return jpaRepository.count(specification) > 0;
    }

    @Override
    public long count(
            Specification<AppointmentEntity> specification
    ) {
        return jpaRepository.count(specification);
    }

}