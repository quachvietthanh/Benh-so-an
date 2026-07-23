package com.benhsoan.appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.benhsoan.persistence.jpaRepository.auth.JpaUserRepository;
import com.benhsoan.persistence.jpaRepository.patient.JpaPatientRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Appointment service tests")
class AppointmentServiceTest {
    private static final ZoneId CLINIC_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    @Mock
    private AppointmentRepository repository;

    @Mock
    private JpaPatientRepository patientRepository;

    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private AppointmentService service;

    @Test
    @DisplayName("Check-in must be rejected outside the appointment date")
    void rejectsCheckInOutsideAppointmentDate() {
        UUID appointmentId = UUID.randomUUID();
        AppointmentEntity appointment = AppointmentEntity.builder()
                .id(appointmentId)
                .status(AppointmentStatus.SCHEDULED)
                .appointmentAt(LocalDate.now(CLINIC_ZONE).plusDays(1)
                        .atTime(9, 0)
                        .atZone(CLINIC_ZONE)
                        .toInstant())
                .build();
        when(repository.findByIdForStatusChange(appointmentId)).thenReturn(Optional.of(appointment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.checkIn(appointmentId)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(repository, never()).save(any(AppointmentEntity.class));
    }

    @Test
    @DisplayName("Public appointment codes are random and match the lookup format")
    void generatesUnpredictableLookupCodes() {
        String first = AppointmentService.newAppointmentCode();
        String second = AppointmentService.newAppointmentCode();

        assertTrue(first.matches("^LH-[A-F0-9]{12}$"));
        assertTrue(second.matches("^LH-[A-F0-9]{12}$"));
        assertNotEquals(first, second);
    }

    @Test
    @DisplayName("No-show eligibility starts exactly 15 minutes after the appointment")
    void appliesNoShowThresholdAtExactBoundary() {
        Instant appointmentAt = Instant.parse("2026-07-23T02:00:00Z");
        Instant threshold = appointmentAt.plus(AppointmentService.NO_SHOW_GRACE_PERIOD);

        assertFalse(AppointmentService.isNoShowEligible(
                appointmentAt,
                threshold.minusMillis(1)
        ));
        assertTrue(AppointmentService.isNoShowEligible(appointmentAt, threshold));
        assertTrue(AppointmentService.isNoShowEligible(
                appointmentAt,
                threshold.plusMillis(1)
        ));
    }

    @Test
    @DisplayName("An overdue scheduled appointment can be marked as no-show")
    void marksOverdueScheduledAppointmentAsNoShow() {
        UUID appointmentId = UUID.randomUUID();
        AppointmentEntity appointment = scheduledAppointment(
                appointmentId,
                Instant.now().minus(Duration.ofMinutes(16))
        );
        when(repository.findByIdForStatusChange(appointmentId)).thenReturn(Optional.of(appointment));
        when(repository.save(any(AppointmentEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponse response = service.noShow(appointmentId);

        assertEquals(AppointmentStatus.NO_SHOW, response.status());
        assertEquals(AppointmentStatus.NO_SHOW, appointment.getStatus());
        assertNotNull(appointment.getUpdatedAt());
        verify(repository).save(appointment);
    }

    @Test
    @DisplayName("No-show is rejected before the 15-minute threshold")
    void rejectsNoShowBeforeThreshold() {
        UUID appointmentId = UUID.randomUUID();
        AppointmentEntity appointment = scheduledAppointment(
                appointmentId,
                Instant.now().minus(Duration.ofMinutes(14))
        );
        when(repository.findByIdForStatusChange(appointmentId)).thenReturn(Optional.of(appointment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.noShow(appointmentId)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(repository, never()).save(any(AppointmentEntity.class));
    }

    @ParameterizedTest(name = "Status {0} cannot be marked as no-show")
    @EnumSource(value = AppointmentStatus.class, names = "SCHEDULED", mode = EnumSource.Mode.EXCLUDE)
    void rejectsNoShowForNonScheduledStatuses(AppointmentStatus status) {
        UUID appointmentId = UUID.randomUUID();
        AppointmentEntity appointment = AppointmentEntity.builder()
                .id(appointmentId)
                .status(status)
                .appointmentAt(Instant.now().minus(Duration.ofHours(1)))
                .build();
        when(repository.findByIdForStatusChange(appointmentId)).thenReturn(Optional.of(appointment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.noShow(appointmentId)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(repository, never()).save(any(AppointmentEntity.class));
    }

    @Test
    @DisplayName("No-show returns not found for an unknown appointment")
    void rejectsNoShowForUnknownAppointment() {
        UUID appointmentId = UUID.randomUUID();
        when(repository.findByIdForStatusChange(appointmentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.noShow(appointmentId)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(repository, never()).save(any(AppointmentEntity.class));
    }

    private AppointmentEntity scheduledAppointment(UUID id, Instant appointmentAt) {
        return AppointmentEntity.builder()
                .id(id)
                .appointmentCode("LH-ABCDEF123456")
                .patientId(UUID.randomUUID())
                .doctorId(UUID.randomUUID())
                .department("Nội tổng quát")
                .appointmentAt(appointmentAt)
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }
}
