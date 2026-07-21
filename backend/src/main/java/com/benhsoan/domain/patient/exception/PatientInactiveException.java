package com.benhsoan.domain.patient.exception;

import org.springframework.http.HttpStatus;

import com.benhsoan.domain.shared.exception.DomainException;

public class PatientInactiveException extends DomainException {

    public PatientInactiveException() {
        super(
                HttpStatus.FORBIDDEN,
                "Patient has been deactivated."
        );
    }

}