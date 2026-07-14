package com.benhsoan.repository;

import com.benhsoan.model.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPatientCode(String patientCode);

    Optional<Patient> findByIdentityNumber(String identityNumber);

    Page<Patient> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.patientCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Patient> searchPatients(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByPatientCode(String patientCode);
}
