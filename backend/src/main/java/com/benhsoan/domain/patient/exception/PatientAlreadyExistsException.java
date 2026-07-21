package com.benhsoan.domain.patient.exception;

import org.springframework.http.HttpStatus;

import com.benhsoan.domain.shared.exception.DomainException;

public class PatientAlreadyExistsException extends DomainException {

    public PatientAlreadyExistsException(String field) {
        super(
                HttpStatus.CONFLICT,
                "Patient already exists with " + field + "."
        );
    }

}