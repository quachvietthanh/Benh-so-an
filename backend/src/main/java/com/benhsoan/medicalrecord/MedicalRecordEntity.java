package com.benhsoan.medicalrecord;

import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "electronic_medical_records")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class MedicalRecordEntity {
    @Id @Column(columnDefinition = "BINARY(16)") private UUID id;
    @Column(name="record_code", nullable=false, unique=true, length=30) private String recordCode;
    @Column(name="patient_id", nullable=false, columnDefinition="BINARY(16)") private UUID patientId;
    @Column(name="doctor_id", nullable=false, columnDefinition="BINARY(16)") private UUID doctorId;
    @Column(nullable=false, columnDefinition="TEXT") private String symptoms;
    @Column(name="examination_note", columnDefinition="TEXT") private String examinationNote;
    @Column(nullable=false, length=500) private String diagnosis;
    @Column(name="treatment_plan", columnDefinition="TEXT") private String treatmentPlan;
    @Column(name="clinical_orders", columnDefinition="json") private String clinicalOrders;
    @Column(name="clinical_results", columnDefinition="json") private String clinicalResults;
    @Column(nullable=false, length=20) private String status;
    @Column(name="created_at", nullable=false) private Instant createdAt;
    @Column(name="updated_at", nullable=false) private Instant updatedAt;
}
