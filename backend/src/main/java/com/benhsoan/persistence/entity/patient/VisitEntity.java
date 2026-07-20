package com.benhsoan.persistence.entity.patient;

import java.time.Instant;
import java.util.UUID;

import com.benhsoan.domain.patient.enums.VisitStatus;
import com.benhsoan.domain.patient.enums.VisitType;

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
@Table(name = "visits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "patient_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID patientId;

    @Column(name = "doctor_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID doctorId;

    @Column(name = "department_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID departmentId;

    @Column(name = "visit_code", nullable = false, unique = true, length = 30)
    private String visitCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_type", nullable = false, length = 20)
    private VisitType visitType;

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_status", nullable = false, length = 20)
    private VisitStatus visitStatus;

    @Column(name = "visit_at", nullable = false)
    private Instant visitAt;

    @Column(name = "reason", nullable = false, length = 255)
    private String reason;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}