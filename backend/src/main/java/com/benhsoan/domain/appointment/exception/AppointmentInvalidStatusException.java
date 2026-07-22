package com.benhsoan.domain.appointment.exception;

import org.springframework.http.HttpStatus;

public class AppointmentInvalidStatusException extends AppointmentException {

    public AppointmentInvalidStatusException(String message) {
        super(HttpStatus.CONFLICT, message);
    }

}