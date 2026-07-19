package com.benhsoan.persistence.jpaRepository.patient;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.benhsoan.persistence.entity.patient.MedicalRecordAccessLogEntity;

public interface JpaMedicalRecordAccessLogRepository
        extends JpaRepository<MedicalRecordAccessLogEntity, UUID> {

    Page<MedicalRecordAccessLogEntity> findByPatientId(
            UUID patientId,
            Pageable pageable
    );

    Page<MedicalRecordAccessLogEntity> findByVisitId(
            UUID visitId,
            Pageable pageable
    );

}