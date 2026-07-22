package com.benhsoan.domain.appointment.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;

public class AppointmentReminderNotFoundException extends AppointmentException {

    public AppointmentReminderNotFoundException(UUID reminderId) {
        super(
                HttpStatus.NOT_FOUND,
                "Appointment reminder not found with id: " + reminderId
        );
    }

}