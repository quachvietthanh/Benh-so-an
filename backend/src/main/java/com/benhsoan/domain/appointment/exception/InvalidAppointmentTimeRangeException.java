package com.benhsoan.domain.appointment.exception;

import org.springframework.http.HttpStatus;

public class InvalidAppointmentTimeRangeException
        extends AppointmentException {

    public InvalidAppointmentTimeRangeException() {
        super(
                HttpStatus.BAD_REQUEST,
                "Appointment end time must be after start time."
        );
    }

}