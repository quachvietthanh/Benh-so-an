package com.benhsoan.domain.appointment.exception;

import org.springframework.http.HttpStatus;

public class AppointmentQueueInvalidStatusException extends AppointmentException {

    public AppointmentQueueInvalidStatusException(String message) {
        super(
                HttpStatus.CONFLICT,
                message
        );
    }

}