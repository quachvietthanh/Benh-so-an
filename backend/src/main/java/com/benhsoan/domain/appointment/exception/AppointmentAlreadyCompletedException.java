package com.benhsoan.domain.appointment.exception;

import org.springframework.http.HttpStatus;

public class AppointmentAlreadyCompletedException extends AppointmentException {

    public AppointmentAlreadyCompletedException() {
        super(
                HttpStatus.CONFLICT,
                "Appointment has already been completed."
        );
    }

}