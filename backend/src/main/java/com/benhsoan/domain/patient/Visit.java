package com.benhsoan.domain.patient;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.benhsoan.domain.patient.enums.VisitStatus;
import com.benhsoan.domain.patient.enums.VisitType;
import com.benhsoan.domain.shared.Guard.Guard;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Visit {

    private UUID id;

    private UUID patientId;

    private UUID doctorId;

    private UUID departmentId;

    private String visitCode;

    private VisitType visitType;

    private VisitStatus visitStatus;

    private Instant visitAt;

    private String reason;

    private String note;

    private Instant createdAt;

    private Instant updatedAt;

    private Visit(
            UUID id,
            UUID patientId,
            UUID doctorId,
            UUID departmentId,
            String visitCode,
            VisitType visitType,
            VisitStatus visitStatus,
            Instant visitAt,
            String reason,
            String note,
            Instant createdAt,
            Instant updatedAt
    ) {

        this.id = Objects.requireNonNull(id);

        this.patientId = Guard.require(patientId, "Patient");
        this.doctorId = Guard.require(doctorId, "Doctor");
        this.departmentId = Guard.require(departmentId, "Department");

        this.visitCode = Guard.require(visitCode, "Visit code");

        this.visitType = Guard.require(visitType, "Visit type");

        this.visitStatus = Guard.require(visitStatus, "Visit status");

        this.visitAt = Guard.require(visitAt, "Visit time");

        this.reason = Guard.require(reason, "Reason");

        this.note = note;

        this.createdAt = Objects.requireNonNull(createdAt);

        this.updatedAt = updatedAt;
    }

    public static Visit create(
            UUID patientId,
            UUID doctorId,
            UUID departmentId,
            String visitCode,
            VisitType visitType,
            Instant visitAt,
            String reason,
            String note
    ) {

        return new Visit(
                UUID.randomUUID(),
                patientId,
                doctorId,
                departmentId,
                visitCode,
                visitType,
                VisitStatus.WAITING,
                visitAt,
                reason,
                note,
                Instant.now(),
                null
        );
    }

    public void update(
            UUID doctorId,
            UUID departmentId,
            VisitType visitType,
            Instant visitAt,
            String reason,
            String note
    ) {

        this.doctorId = Guard.require(doctorId, "Doctor");

        this.departmentId = Guard.require(departmentId, "Department");

        this.visitType = Guard.require(visitType, "Visit type");

        this.visitAt = Guard.require(visitAt, "Visit time");

        this.reason = Guard.require(reason, "Reason");

        this.note = note;

        this.updatedAt = Instant.now();
    }

    public void updateStatus(VisitStatus status) {

        this.visitStatus = Guard.require(status, "Visit status");

        this.updatedAt = Instant.now();
    }

    public static Visit restore(
            UUID id,
            UUID patientId,
            UUID doctorId,
            UUID departmentId,
            String visitCode,
            VisitType visitType,
            VisitStatus visitStatus,
            Instant visitAt,
            String reason,
            String note,
            Instant createdAt,
            Instant updatedAt
    ) {

        return new Visit(
                id,
                patientId,
                doctorId,
                departmentId,
                visitCode,
                visitType,
                visitStatus,
                visitAt,
                reason,
                note,
                createdAt,
                updatedAt
        );
    }

}