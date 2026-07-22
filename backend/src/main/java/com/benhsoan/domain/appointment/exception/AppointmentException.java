package com.benhsoan.domain.appointment.exception;

import org.springframework.http.HttpStatus;

import com.benhsoan.domain.shared.exception.DomainException;

public abstract class AppointmentException extends DomainException {

    protected AppointmentException(
            HttpStatus status,
            String message
    ) {
        super(status, message);
    }

}