package com.benhsoan.domain.appointment.exception;

import org.springframework.http.HttpStatus;

public class AppointmentAlreadyCancelledException extends AppointmentException {

    public AppointmentAlreadyCancelledException() {
        super(
                HttpStatus.CONFLICT,
                "Appointment has already been cancelled."
        );
    }

}