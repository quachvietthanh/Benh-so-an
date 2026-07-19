package com.benhsoan.port.outbound.repository.logRepository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.benhsoan.domain.patient.MedicalRecordAccessLog;

public interface MedicalRecordAccessLogRepository {

    MedicalRecordAccessLog save(
            MedicalRecordAccessLog log
    );

    Optional<MedicalRecordAccessLog> findById(UUID id);

    Page<MedicalRecordAccessLog> findAll(Pageable pageable);

    Page<MedicalRecordAccessLog> findByPatientId(
            UUID patientId,
            Pageable pageable
    );

    Page<MedicalRecordAccessLog> findByVisitId(
            UUID visitId,
            Pageable pageable
    );

}