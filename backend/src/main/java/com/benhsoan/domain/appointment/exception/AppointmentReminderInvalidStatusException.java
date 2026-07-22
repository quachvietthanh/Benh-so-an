package com.benhsoan.domain.appointment.exception;

import org.springframework.http.HttpStatus;

public class AppointmentReminderInvalidStatusException extends AppointmentException {

    public AppointmentReminderInvalidStatusException(String message) {
        super(
                HttpStatus.CONFLICT,
                message
        );
    }

}