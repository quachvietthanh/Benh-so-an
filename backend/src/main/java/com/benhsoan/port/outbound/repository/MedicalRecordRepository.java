package com.benhsoan.repository;

import com.benhsoan.model.entity.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    Page<MedicalRecord> findByPatientId(Long patientId, Pageable pageable);

    List<MedicalRecord> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    Page<MedicalRecord> findByDoctorId(Long doctorId, Pageable pageable);

    Page<MedicalRecord> findByStatus(MedicalRecord.RecordStatus status, Pageable pageable);
}
