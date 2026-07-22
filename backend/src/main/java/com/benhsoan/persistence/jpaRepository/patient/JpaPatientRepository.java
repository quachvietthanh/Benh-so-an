package com.benhsoan.persistence.jpaRepository.patient;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.benhsoan.persistence.entity.patient.PatientEntity;

public interface JpaPatientRepository extends JpaRepository<PatientEntity, UUID>, JpaSpecificationExecutor<PatientEntity> {

    Optional<PatientEntity> findByPatientCode(String patientCode);

    Page<PatientEntity> findByFullNameContainingIgnoreCase(
            String keyword,
            Pageable pageable
    );

    boolean existsByPatientCode(String patientCode);

    boolean existsByIdentityNumber(String identityNumber);

    Optional<PatientEntity> findTopByOrderByPatientCodeDesc();

    boolean existsByIdentityNumberAndIdNot( String identityNumber, UUID id);

    Optional<PatientEntity> findByUserId(UUID userId);

    
}
