package com.benhsoan.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.benhsoan.domain.shared.exception.DomainException;

public class EmailAlreadyExistsException extends DomainException {

    public EmailAlreadyExistsException() {
        super(
                HttpStatus.CONFLICT,
                "Email already exists."
        );
    }
}