package com.benhsoan.domain.appointment.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedAppointmentOperationException extends AppointmentException {

    public UnauthorizedAppointmentOperationException() {
        super(
                HttpStatus.FORBIDDEN,
                "You are not authorized to perform this appointment operation."
        );
    }

}