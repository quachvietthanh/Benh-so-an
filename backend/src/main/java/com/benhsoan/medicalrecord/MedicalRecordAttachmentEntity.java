package com.benhsoan.medicalrecord;

import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="medical_record_attachments")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class MedicalRecordAttachmentEntity {
    @Id @Column(columnDefinition="BINARY(16)") private UUID id;
    @Column(name="record_id", nullable=false, columnDefinition="BINARY(16)") private UUID recordId;
    @Column(name="file_name", nullable=false) private String fileName;
    @Column(name="content_type", nullable=false, length=100) private String contentType;
    @Column(name="file_size", nullable=false) private long fileSize;
    @Lob @Column(name="file_data", nullable=false, columnDefinition="LONGBLOB") private byte[] fileData;
    @Column(name="uploaded_at", nullable=false) private Instant uploadedAt;
}
