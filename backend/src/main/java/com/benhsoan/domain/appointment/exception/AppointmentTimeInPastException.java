package com.benhsoan.domain.appointment.exception;

import org.springframework.http.HttpStatus;

public class AppointmentTimeInPastException extends AppointmentException {

    public AppointmentTimeInPastException() {
        super(
                HttpStatus.BAD_REQUEST,
                "Appointment time cannot be in the past."
        );
    }

}