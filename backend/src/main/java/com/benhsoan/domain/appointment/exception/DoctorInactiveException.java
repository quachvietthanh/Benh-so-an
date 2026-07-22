package com.benhsoan.domain.appointment.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;

public class DoctorInactiveException extends AppointmentException {

    public DoctorInactiveException(UUID doctorId) {
        super(
                HttpStatus.CONFLICT,
                "Doctor is inactive: " + doctorId
        );
    }

}