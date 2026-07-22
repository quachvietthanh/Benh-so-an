package com.benhsoan.port.dto.command.patient;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record GetPatientMedicalHistoryQuery(

        @NotNull
        UUID patientId,

        Instant fromDate,

        Instant toDate,

        int page,

        int size

) {

    public GetPatientMedicalHistoryQuery {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (size > 100) size = 100;
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "fromDate must not be after toDate"
            );
        }
    }

    public static GetPatientMedicalHistoryQuery of(
            UUID patientId,
            Instant fromDate,
            Instant toDate,
            Integer page,
            Integer size
    ) {
        return new GetPatientMedicalHistoryQuery(
                patientId,
                fromDate,
                toDate,
                page != null ? page : 0,
                size != null ? size : 10
        );
    }
}
