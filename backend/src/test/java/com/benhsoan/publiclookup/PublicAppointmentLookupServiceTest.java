package com.benhsoan.publiclookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.benhsoan.appointment.AppointmentRepository;
import com.benhsoan.publiclookup.PublicAppointmentLookupResponse.CareState;

@ExtendWith(MockitoExtension.class)
@DisplayName("Public appointment lookup service tests")
class PublicAppointmentLookupServiceTest {
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1990, 5, 12);
    private static final Instant APPOINTMENT_AT = Instant.parse("2026-07-23T02:00:00Z");

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentRepository.PublicLookupProjection projection;

    @InjectMocks
    private PublicAppointmentLookupService lookupService;

    @ParameterizedTest
    @CsvSource({
            "SCHEDULED,SCHEDULED",
            "CHECKED_IN,IN_PROGRESS",
            "CALLED,IN_PROGRESS",
            "COMPLETED,COMPLETED",
            "CANCELLED,UNAVAILABLE",
            "NO_SHOW,UNAVAILABLE"
    })
    @DisplayName("Internal statuses are mapped to privacy-safe public states")
    void mapsInternalStatuses(String internalStatus, CareState expectedState) {
        when(appointmentRepository.findPublicLookup("LH-1234567890", DATE_OF_BIRTH))
                .thenReturn(Optional.of(projection));
        when(projection.getStatus()).thenReturn(internalStatus);
        when(projection.getAppointmentAt()).thenReturn(Timestamp.from(APPOINTMENT_AT));

        PublicAppointmentLookupResponse response = lookupService.lookup(
                new PublicAppointmentLookupRequest(" lh-1234567890 ", DATE_OF_BIRTH));

        assertTrue(response.matched());
        assertEquals(expectedState, response.careState());
        assertEquals(APPOINTMENT_AT, response.scheduledAt());
        verify(appointmentRepository).findPublicLookup("LH-1234567890", DATE_OF_BIRTH);
    }

    @Test
    @DisplayName("Unknown code and verification mismatch return the same generic result")
    void returnsGenericResultWhenNoVerifiedMatchExists() {
        LocalDate wrongDateOfBirth = DATE_OF_BIRTH.plusDays(1);
        when(appointmentRepository.findPublicLookup("LH-NOT-FOUND", DATE_OF_BIRTH))
                .thenReturn(Optional.empty());
        when(appointmentRepository.findPublicLookup("LH-1234567890", wrongDateOfBirth))
                .thenReturn(Optional.empty());

        PublicAppointmentLookupResponse unknownCode = lookupService.lookup(
                new PublicAppointmentLookupRequest("LH-NOT-FOUND", DATE_OF_BIRTH));
        PublicAppointmentLookupResponse wrongDate = lookupService.lookup(
                new PublicAppointmentLookupRequest("LH-1234567890", wrongDateOfBirth));

        assertEquals(unknownCode, wrongDate);
        assertFalse(unknownCode.matched());
        assertNull(unknownCode.careState());
        assertNull(unknownCode.scheduledAt());
    }
}
