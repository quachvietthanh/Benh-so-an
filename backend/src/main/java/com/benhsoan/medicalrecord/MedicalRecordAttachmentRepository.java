package com.benhsoan.medicalrecord;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordAttachmentRepository extends JpaRepository<MedicalRecordAttachmentEntity, UUID> {
    List<MedicalRecordAttachmentEntity> findByRecordIdOrderByUploadedAt(UUID recordId);
}
