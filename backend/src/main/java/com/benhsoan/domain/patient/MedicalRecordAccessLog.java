package com.benhsoan.domain.patient;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.benhsoan.domain.patient.enums.MedicalRecordAccessAction;
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
public class MedicalRecordAccessLog {

    private UUID id;

    private UUID patientId;

    private UUID visitId;

    private UUID accessedBy;

    private MedicalRecordAccessAction action;

    private String ipAddress;

    private Instant accessedAt;

    private MedicalRecordAccessLog(
            UUID id,
            UUID patientId,
            UUID visitId,
            UUID accessedBy,
            MedicalRecordAccessAction action,
            String ipAddress,
            Instant accessedAt
    ) {

        this.id = Objects.requireNonNull(id);

        this.patientId = Guard.require(patientId, "Patient");

        this.visitId = Guard.require(visitId, "Visit");

        this.accessedBy = Guard.require(accessedBy, "Accessed by");

        this.action = Guard.require(action, "Action");

        this.ipAddress = ipAddress;

        this.accessedAt = Objects.requireNonNull(accessedAt);
    }

    public static MedicalRecordAccessLog create(
            UUID patientId,
            UUID visitId,
            UUID accessedBy,
            MedicalRecordAccessAction action,
            String ipAddress
    ) {

        return new MedicalRecordAccessLog(
                UUID.randomUUID(),
                patientId,
                visitId,
                accessedBy,
                action,
                ipAddress,
                Instant.now()
        );
    }

    public static MedicalRecordAccessLog restore(
            UUID id,
            UUID patientId,
            UUID visitId,
            UUID accessedBy,
            MedicalRecordAccessAction action,
            String ipAddress,
            Instant accessedAt
    ) {

        return new MedicalRecordAccessLog(
                id,
                patientId,
                visitId,
                accessedBy,
                action,
                ipAddress,
                accessedAt
        );
    }

}