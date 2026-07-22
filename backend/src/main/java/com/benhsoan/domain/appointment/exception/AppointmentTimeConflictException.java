package com.benhsoan.domain.appointment.exception;

import org.springframework.http.HttpStatus;

public class AppointmentTimeConflictException extends AppointmentException {

    public AppointmentTimeConflictException() {
        super(
                HttpStatus.CONFLICT,
                "Doctor already has an appointment during the selected time."
        );
    }

}