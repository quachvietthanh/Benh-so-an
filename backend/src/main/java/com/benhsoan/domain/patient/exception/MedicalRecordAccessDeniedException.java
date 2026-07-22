package com.benhsoan.domain.patient.exception;

import org.springframework.http.HttpStatus;

import com.benhsoan.domain.shared.exception.DomainException;

public class MedicalRecordAccessDeniedException
        extends DomainException {

    public MedicalRecordAccessDeniedException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
