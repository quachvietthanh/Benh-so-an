package com.benhsoan.domain.appointment.exception;

import org.springframework.http.HttpStatus;

public class AppointmentAlreadyInProgressException extends AppointmentException {

    public AppointmentAlreadyInProgressException() {
        super(
                HttpStatus.CONFLICT,
                "Appointment has already been in progress."
        );
    }

}