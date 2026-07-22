package com.benhsoan.domain.appointment.exception;

import org.springframework.http.HttpStatus;

public class AppointmentQueueAlreadyCompletedException extends AppointmentException {

    public AppointmentQueueAlreadyCompletedException() {
        super(
                HttpStatus.CONFLICT,
                "Appointment queue has already been completed."
        );
    }

}