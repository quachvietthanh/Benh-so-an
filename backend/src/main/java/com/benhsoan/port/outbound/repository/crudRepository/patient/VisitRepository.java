package com.benhsoan.port.outbound.repository.crudRepository.patient;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.benhsoan.domain.patient.Visit;
import com.benhsoan.port.outbound.repository.BaseRepository;

public interface VisitRepository extends BaseRepository<Visit, UUID> {

    Page<Visit> findAll(Pageable pageable);

    Optional<Visit> findByVisitCode(String visitCode);

    Page<Visit> findByPatientId(UUID patientId, Pageable pageable);

    Page<Visit> findByDoctorId(UUID doctorId, Pageable pageable);

    boolean existsByVisitCode(String visitCode);

    Page<Visit> findByPatientIdWithDateFilter(
            UUID patientId,
            Instant fromDate,
            Instant toDate,
            Pageable pageable
    );

    boolean existsByPatientIdAndDoctorId(
            UUID patientId,
            UUID doctorId
    );
}
