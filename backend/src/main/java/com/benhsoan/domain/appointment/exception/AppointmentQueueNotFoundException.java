package com.benhsoan.domain.appointment.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;

public class AppointmentQueueNotFoundException extends AppointmentException {

    public AppointmentQueueNotFoundException(UUID queueId) {
        super(
                HttpStatus.NOT_FOUND,
                "Appointment queue not found with id: " + queueId
        );
    }

}