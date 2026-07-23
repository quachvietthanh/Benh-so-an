package com.benhsoan.publiclookup;

import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.appointment.AppointmentRepository;
import com.benhsoan.publiclookup.PublicAppointmentLookupResponse.CareState;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicAppointmentLookupService {
    private final AppointmentRepository appointmentRepository;

    public PublicAppointmentLookupResponse lookup(PublicAppointmentLookupRequest request) {
        String normalizedCode = request.appointmentCode().trim().toUpperCase(Locale.ROOT);

        return appointmentRepository.findPublicLookup(normalizedCode, request.dateOfBirth())
                .map(result -> new PublicAppointmentLookupResponse(
                        true,
                        toCareState(result.getStatus()),
                        result.getAppointmentAt().toInstant()))
                .orElseGet(PublicAppointmentLookupResponse::notFound);
    }

    CareState toCareState(String status) {
        return switch (status) {
            case "SCHEDULED" -> CareState.SCHEDULED;
            case "CHECKED_IN", "CALLED" -> CareState.IN_PROGRESS;
            case "COMPLETED" -> CareState.COMPLETED;
            case "CANCELLED", "NO_SHOW" -> CareState.UNAVAILABLE;
            default -> CareState.UNAVAILABLE;
        };
    }
}
