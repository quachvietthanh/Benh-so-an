package com.benhsoan.domain.appointment.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;

public class AppointmentNotFoundException extends AppointmentException {

    public AppointmentNotFoundException(UUID appointmentId) {
        super(
                HttpStatus.NOT_FOUND,
                "Appointment not found with id: " + appointmentId
        );
    }

}