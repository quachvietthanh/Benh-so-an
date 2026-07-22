package com.benhsoan.medicalrecord;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecordEntity, UUID> {
    List<MedicalRecordEntity> findAllByOrderByCreatedAtDesc();
    List<MedicalRecordEntity> findByPatientIdOrderByCreatedAtDesc(UUID patientId);
}
