package com.benhsoan.persistence.entity.patient;

import java.time.Instant;
import java.util.UUID;

import com.benhsoan.domain.patient.enums.MedicalRecordAccessAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "medical_record_access_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordAccessLogEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "patient_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID patientId;

    @Column(name = "visit_id", nullable = true, columnDefinition = "BINARY(16)")
    private UUID visitId;

    @Column(name = "accessed_by", nullable = false, columnDefinition = "BINARY(16)")
    private UUID accessedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private MedicalRecordAccessAction action;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "accessed_at", nullable = false)
    private Instant accessedAt;

}
