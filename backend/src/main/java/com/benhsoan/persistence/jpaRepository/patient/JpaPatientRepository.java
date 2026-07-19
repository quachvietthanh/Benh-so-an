package com.benhsoan.persistence.jpaRepository.patient;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.benhsoan.persistence.entity.patient.PatientEntity;

public interface JpaPatientRepository extends JpaRepository<PatientEntity, UUID> {

    Optional<PatientEntity> findByPatientCode(String patientCode);

    Page<PatientEntity> findByFullNameContainingIgnoreCase(
            String keyword,
            Pageable pageable
    );

    boolean existsByPatientCode(String patientCode);

    boolean existsByIdentityNumber(String identityNumber);

}