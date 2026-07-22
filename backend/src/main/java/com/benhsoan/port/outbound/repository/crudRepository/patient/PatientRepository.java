package com.benhsoan.port.outbound.repository.crudRepository.patient;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.benhsoan.domain.patient.Patient;
import com.benhsoan.port.outbound.repository.BaseRepository;

public interface PatientRepository extends BaseRepository<Patient, UUID> {

    Page<Patient> findAll(Pageable pageable);

    Optional<Patient> findByPatientCode(String patientCode);

    Page<Patient> findByFullNameContaining(String keyword, Pageable pageable);

    boolean existsByPatientCode(String patientCode);

    boolean existsByIdentityNumber(String identityNumber);

    Optional<Patient> findTopByOrderByPatientCodeDesc();

    boolean existsByIdentityNumberAndIdNot( String identityNumber, UUID id);

    Optional<Patient> findByUserId(UUID userId);
}
