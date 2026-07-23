package com.benhsoan.publiclookup;

import java.time.Instant;

public record PublicAppointmentLookupResponse(
        boolean matched,
        CareState careState,
        Instant scheduledAt
) {
    public enum CareState {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        UNAVAILABLE
    }

    public static PublicAppointmentLookupResponse notFound() {
        return new PublicAppointmentLookupResponse(false, null, null);
    }
}
