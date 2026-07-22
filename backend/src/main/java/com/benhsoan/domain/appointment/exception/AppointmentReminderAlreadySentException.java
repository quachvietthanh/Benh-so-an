package com.benhsoan.domain.appointment.exception;

import org.springframework.http.HttpStatus;

public class AppointmentReminderAlreadySentException extends AppointmentException {

    public AppointmentReminderAlreadySentException() {
        super(
                HttpStatus.CONFLICT,
                "Appointment reminder has already been sent."
        );
    }

}