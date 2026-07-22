package com.benhsoan.domain.appointment.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;

public class DoctorNotFoundException extends AppointmentException {

    public DoctorNotFoundException(UUID doctorId) {
        super(
                HttpStatus.NOT_FOUND,
                "Doctor not found: " + doctorId
        );
    }

}