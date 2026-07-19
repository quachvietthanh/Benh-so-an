package com.benhsoan.persistence.jpaRepository.patient;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.benhsoan.persistence.entity.patient.VisitEntity;

public interface JpaVisitRepository extends JpaRepository<VisitEntity, UUID> {

    Optional<VisitEntity> findByVisitCode(String visitCode);

    Page<VisitEntity> findByPatientId(
            UUID patientId,
            Pageable pageable
    );

    Page<VisitEntity> findByDoctorId(
            UUID doctorId,
            Pageable pageable
    );

    boolean existsByVisitCode(String visitCode);

}